import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { map, Observable } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICustomer, NewCustomer } from '../customer.model';

export type PartialUpdateCustomer = Partial<ICustomer> & Pick<ICustomer, 'id'>;

type RestOf<T extends ICustomer | NewCustomer> = Omit<T, 'date'> & {
  date?: string | null;
};

export type RestCustomer = RestOf<ICustomer>;

export type NewRestCustomer = RestOf<NewCustomer>;

export type PartialUpdateRestCustomer = RestOf<PartialUpdateCustomer>;

export type EntityResponseType = HttpResponse<ICustomer>;
export type EntityArrayResponseType = HttpResponse<ICustomer[]>;

@Injectable({ providedIn: 'root' })
export class CustomerService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/customers');

  create(customer: NewCustomer): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(customer);
    return this.http
      .post<RestCustomer>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(customer: ICustomer): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(customer);
    return this.http
      .put<RestCustomer>(`${this.resourceUrl}/${this.getCustomerIdentifier(customer)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(customer: PartialUpdateCustomer): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(customer);
    return this.http
      .patch<RestCustomer>(`${this.resourceUrl}/${this.getCustomerIdentifier(customer)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http
      .get<RestCustomer>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestCustomer[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getCustomerIdentifier(customer: Pick<ICustomer, 'id'>): string {
    return customer.id;
  }

  compareCustomer(o1: Pick<ICustomer, 'id'> | null, o2: Pick<ICustomer, 'id'> | null): boolean {
    return o1 && o2 ? this.getCustomerIdentifier(o1) === this.getCustomerIdentifier(o2) : o1 === o2;
  }

  addCustomerToCollectionIfMissing<Type extends Pick<ICustomer, 'id'>>(
    customerCollection: Type[],
    ...customersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const customers: Type[] = customersToCheck.filter(isPresent);
    if (customers.length > 0) {
      const customerCollectionIdentifiers = customerCollection.map(customerItem => this.getCustomerIdentifier(customerItem));
      const customersToAdd = customers.filter(customerItem => {
        const customerIdentifier = this.getCustomerIdentifier(customerItem);
        if (customerCollectionIdentifiers.includes(customerIdentifier)) {
          return false;
        }
        customerCollectionIdentifiers.push(customerIdentifier);
        return true;
      });
      return [...customersToAdd, ...customerCollection];
    }
    return customerCollection;
  }

  protected convertDateFromClient<T extends ICustomer | NewCustomer | PartialUpdateCustomer>(customer: T): RestOf<T> {
    return {
      ...customer,
      date: customer.date?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restCustomer: RestCustomer): ICustomer {
    return {
      ...restCustomer,
      date: restCustomer.date ? dayjs(restCustomer.date) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestCustomer>): HttpResponse<ICustomer> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestCustomer[]>): HttpResponse<ICustomer[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
