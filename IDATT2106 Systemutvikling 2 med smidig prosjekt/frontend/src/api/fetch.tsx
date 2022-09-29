import { getCookie } from 'api/cookie';
import { API_URL, ACCESS_TOKEN, REFRESH_TOKEN } from 'constant';
import { RequestResponse } from 'types/Types';
import API from 'api/api';

type RequestMethodType = 'GET' | 'POST' | 'PUT' | 'DELETE';

type FetchProps = {
  method: RequestMethodType;
  url: string;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  data?: Record<string, unknown | any>;
  withAuth?: boolean;
  file?: File | Blob;
  refreshAccess?: boolean;
  tryAgain?: boolean;
};

// eslint-disable-next-line comma-spacing
export const IFetch = <T,>({ method, url, data = {}, withAuth = true, refreshAccess = false, tryAgain = true, file }: FetchProps): Promise<T> => {
  const urlAddress = API_URL + url;
  const headers = new Headers();
  if (!file) {
    headers.append('Content-Type', 'application/json');
  }

  if (refreshAccess) {
    headers.append('authorization', `Bearer ${getCookie(REFRESH_TOKEN)}`);
  } else if (withAuth) {
    headers.append('authorization', `Bearer ${getCookie(ACCESS_TOKEN)}`);
  }

  return fetch(request(method, file ? url : urlAddress, headers, data, file)).then(async (response) => {
    const contentType = response.headers.get('content-type');
    if (!contentType || !contentType.includes('application/json') || !response.ok || response.json === undefined) {
      if (response.status === 401 && Boolean(getCookie(REFRESH_TOKEN)) && !refreshAccess && tryAgain) {
        await API.refreshAccessToken();
        return IFetch<T>({ method, url, data, withAuth, file, tryAgain: false });
      } else if (response.json) {
        return response.json().then((responseData: RequestResponse) => {
          throw responseData;
        });
      } else {
        throw { message: response.statusText } as RequestResponse;
      }
    }
    return response.json().then((responseData: T) => responseData);
  });
};
const request = (method: RequestMethodType, url: string, headers: Headers, data: Record<string, unknown>, file?: File | Blob) => {
  const getBody = () => {
    if (file) {
      const data = new FormData();
      data.append('image', file);
      return data;
    } else {
      return method !== 'GET' ? JSON.stringify(data) : undefined;
    }
  };
  const requestUrl = method === 'GET' ? url + argsToParams(data) : url;
  return new Request(requestUrl, {
    method: method,
    headers: headers,
    body: getBody(),
  });
};

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const argsToParams = (data: Record<string, any>) => {
  let args = '?';
  for (const key in data) {
    if (Array.isArray(data[key])) {
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      for (const value in data[key] as any) {
        args += `&${key}=${data[key][value]}`;
      }
    } else {
      args += `&${key}=${data[key]}`;
    }
  }
  return args;
};
