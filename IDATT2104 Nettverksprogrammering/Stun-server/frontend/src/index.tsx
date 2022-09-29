import ReactDOM from 'react-dom';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import URLS from 'URLS';

// Theme
import { MuiThemeProvider } from '@material-ui/core/styles';
import theme from 'theme';
import 'assets/css/main.css';

// Service and action imports
import { StoreProvider } from 'store';
import { SnackbarProvider } from 'Snackbar';

// Project containers
import Landing from 'containers/Landing';
import Room from 'containers/Room';

const App = () => {
  return (
    <StoreProvider>
      <BrowserRouter>
        <MuiThemeProvider theme={theme}>
          <SnackbarProvider>
            <Routes>
              <Route element={<Landing />} path={URLS.landing} />
              <Route element={<Room />} path={`${URLS.room}:id/`} />
            </Routes>
          </SnackbarProvider>
        </MuiThemeProvider>
      </BrowserRouter>
    </StoreProvider>
  );
};

ReactDOM.render(<App />, document.getElementById('root'));
