import { stringify } from "query-string";
import simpleRestProvider from "ra-data-simple-rest";
import { fetchUtils } from "ra-core";

let URL = "http://localhost:2080"; // Caddy
//let URL = "http://localhost:8080"; // Tomcat

export const dataProvider = simpleRestProvider(URL);

let httpClient = fetchUtils.fetchJson;

/*
        .getList = (resource, params ) => {
        let {page: o, perPage: i} = params.pagination, {field: d, order: y} = params.sort, $ = (o - 1) * i, f = o * i - 1,
            l = {sort: JSON.stringify([d, y]), range: JSON.stringify([$, f]), filter: JSON.stringify(params.filter)},
            c = `${n}/${resource}?${(0, h.stringify)(l)}`,
            p = s === "Content-Range" ? {headers: new Headers({Range: `${resource}=${$}-${f}`})} : {};
        return e(c, p).then(({headers: a, json: S}) => {
            if (!a.has(s)) throw new Error(`The ${s} header is missing in the HTTP Response. The simple REST data provider expects responses for lists of resources to contain this header with the total number of results to build the pagination. If you are using CORS, did you declare ${s} in the Access-Control-Expose-Headers header?`);
            return {
                data: S,
                total: s === "Content-Range" ? parseInt(a.get("content-range").split("/").pop(), 10) : parseInt(a.get(s.toLowerCase()))
            }
        })
    */

dataProvider.getList = (resource, params) => {
  const { page, perPage } = params.pagination || { page: 1, perPage: 10 };
  const { field, order } = params.sort || { field: "id", order: "ASC" };

  const rangeStart = (page - 1) * perPage;
  const rangeEnd = page * perPage - 1;

  let q = params.filter.q;
  delete params.filter.q;
  const query = {
    page: page - 1,
    size: perPage,
    //sort: JSON.stringify([field, order]),
    sort: field,
    order: order,
    //range: JSON.stringify([rangeStart, rangeEnd]),
    q: q,
    filter: JSON.stringify(params.filter),
  };
  delete params.filter.q;
  //debugger
  const url = `${URL}/${resource}?${stringify(query)}`;
  const options = {
    signal: params.signal,
  };
  /*
                    countHeader === 'Content-Range'
                        ? {
                            // Chrome doesn't return `Content-Range` header if no `Range` is provided in the request.
                            headers: new Headers({
                                Range: `${resource}=${rangeStart}-${rangeEnd}`,
                            }),
                            signal: params?.signal,
                        }
                        : {signal: params?.signal};
        */
  let countHeader = "Content-Range";
  return httpClient(url, options).then(({ headers, json }) => {
    if (!headers.has(countHeader)) {
      throw new Error(
        `The ${countHeader} header is missing in the HTTP Response. The simple REST data provider expects responses for lists of resources to contain this header with the total number of results to build the pagination. If you are using CORS, did you declare ${countHeader} in the Access-Control-Expose-Headers header?`
      );
    }
    return {
      data: json,
      total:
        countHeader === "Content-Range"
          ? parseInt(headers.get("content-range")!.split("/").pop() || "", 10)
          : parseInt(headers.get(countHeader.toLowerCase())!),
    };
  });
};

dataProvider.getMany = function (resource, params) {
  // var query = {
  //   ids: JSON.stringify({ id: params.ids }),
  //   //_ids: params.ids.join(","),
  // };
  // var idParams = params.ids.map((id) => "id=" + id).join("&");
  //var url = "".concat(URL, "/").concat(resource, "/many?").concat(stringify(query));
  //var url = "".concat(URL, "/").concat(resource, "/many?").concat(idParams);
  var url = ""
    .concat(URL, "/")
    .concat(resource, "/ids/")
    .concat(params.ids.join(","));
  // var ids = encodeURIComponent(JSON.stringify(params.ids))
  // var url = "".concat(URL, "/").concat(resource, "/ids/").concat(ids);
  return httpClient(url, {
    signal: params === null || params === void 0 ? void 0 : params.signal,
  }).then(function (_a) {
    var json = _a.json;
    return {
      data: json,
    };
  });
};

dataProvider.getManyReference = function (resource, params) {
  const { page, perPage } = params.pagination;
  const { field, order } = params.sort;

  const rangeStart = (page - 1) * perPage;
  const rangeEnd = page * perPage - 1;

  let q = params.filter.q;
  delete params.filter.q;
  params.filter[params.target] = params.id;
  const query = {
    page: page - 1,
    size: perPage,
    //sort: JSON.stringify([field, order]),
    sort: field,
    order: order,
    //range: JSON.stringify([rangeStart, rangeEnd]),
    q: q,
    filter: JSON.stringify(params.filter),
    //[params.target]: params.id,
  };
  delete params.filter.q;
  //debugger
  const url = `${URL}/${resource}?${stringify(query)}`;
  const options = {
    signal: params.signal,
  };

  /*
  const query = {
    sort: JSON.stringify([field, order]),
    range: JSON.stringify([(page - 1) * perPage, page * perPage - 1]),
    filter: JSON.stringify({
      ...params.filter,
      [params.target]: params.id,
    }),
  };
  const url = `${apiUrl}/${resource}?${stringify(query)}`;
  const options =
      countHeader === 'Content-Range'
          ? {
            // Chrome doesn't return `Content-Range` header if no `Range` is provided in the request.
            headers: new Headers({
              Range: `${resource}=${rangeStart}-${rangeEnd}`,
            }),
            signal: params?.signal,
          }
          : { signal: params?.signal };
*/

  let countHeader = "Content-Range";
  return httpClient(url, options).then(({ headers, json }) => {
    if (!headers.has(countHeader)) {
      throw new Error(
        `The ${countHeader} header is missing in the HTTP Response. The simple REST data provider expects responses for lists of resources to contain this header with the total number of results to build the pagination. If you are using CORS, did you declare ${countHeader} in the Access-Control-Expose-Headers header?`
      );
    }
    return {
      data: json,
      total:
        countHeader === "Content-Range"
          ? parseInt(headers.get("content-range")!.split("/").pop() || "", 10)
          : parseInt(headers.get(countHeader.toLowerCase())!),
    };
  });
};
