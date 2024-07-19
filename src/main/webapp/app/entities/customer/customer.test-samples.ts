import dayjs from 'dayjs/esm';

import { ICustomer, NewCustomer } from './customer.model';

export const sampleWithRequiredData: ICustomer = {
  id: '47cb33a9-73f1-423e-b45c-649c026ef38f',
};

export const sampleWithPartialData: ICustomer = {
  id: 'c9d78243-46ca-4cf3-8e1c-7f3df613ad54',
  department: 'drat discipline sans',
  date: dayjs('2024-07-17T12:37'),
};

export const sampleWithFullData: ICustomer = {
  id: '3731c109-b2b3-49ec-ab4a-c96203e2c6ad',
  fullName: 'supersede than shrilly',
  customerId: 25082,
  department: 'malnutrition',
  sessionType: 'Morning',
  telephone: '(382) 817-4608 x2388',
  date: dayjs('2024-07-17T16:56'),
};

export const sampleWithNewData: NewCustomer = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
