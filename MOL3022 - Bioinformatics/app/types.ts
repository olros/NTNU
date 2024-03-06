export type PaginationResponse<Type> = {
  count: number;
  next: string;
  previous: string;
  results: Type[];
};

export type Matrix = {
  matrix_id: string;
  name: string;
  collection: string;
  base_id: string;
  version: string;
  sequence_logo: string;
  url: string;
};

export type MatrixDetails = {
  collection: string;
  versions_url: string;
  class: string[];
  pazar_tf_id: number[];
  base_id: string;
  version: number;
  species: { name: string; tax_id: number }[];
  tffm: {
    base_id: string;
    experiment_name: string;
    version: number;
    log_p_detailed: number;
    log_p_1st_order: number;
    tffm_id: string;
    tffm_url: string;
  } | null;
  tfe_ids: number[];
  pfm: {
    T: number[];
    A: number[];
    C: number[];
    G: number[];
  };
  medline: string[];
  name: string;
  uniprot_ids: string[];
  tax_group: string;
  source: string;
  remap_tf_name: string;
  tfe_id: number[];
  family: string[];
  pazar_tf_ids: string[];
  sites_url: string | null;
  matrix_id: string;
  type: string;
  sequence_logo: string;
  pubmed_ids: string[];
};
