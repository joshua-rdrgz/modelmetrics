import axiosModule, { AxiosRequestConfig } from 'axios';

export interface EntityChangeResponse {
  httpStatusCode: string;
  status: string;
  message: string;
}

const axios = axiosModule.create({
  timeout: 5000,
});

/**
 * Request function responsible for making requests to
 * ModelMetrics API.
 * @param config Standard Axios Configuration
 * @returns data from response object, i.e. `response.data`
 */
export async function request(config: AxiosRequestConfig) {
  const response = await axios({ ...config });
  return response.data;
}
