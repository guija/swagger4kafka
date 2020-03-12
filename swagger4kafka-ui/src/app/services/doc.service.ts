import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {AsyncApiDoc} from '../shared/async-api/doc.model';
import {BASE_URL} from '../shared/global';

@Injectable()
export class AsyncApiDocService {

  constructor(private http: HttpClient) { }

  getAsyncApiDoc(): Observable<AsyncApiDoc> {
    return this.http
      .get(BASE_URL + '/doc')
      .pipe(map(doc => doc as AsyncApiDoc));
  }

}
