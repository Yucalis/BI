// @ts-ignore
/* eslint-disable */
import { request } from 'umi';

/** add GET /api/pool/add */
export async function addUsingGET(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.addUsingGETParams,
  options?: { [key: string]: any },
) {
  return request<any>('/api/pool/add', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** get GET /api/pool/get */
export async function getUsingGET(options?: { [key: string]: any }) {
  return request<string>('/api/pool/get', {
    method: 'GET',
    ...(options || {}),
  });
}
