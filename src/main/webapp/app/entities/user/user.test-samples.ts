import { IUser } from './user.model';

export const sampleWithRequiredData: IUser = {
  id: '0cc8f62b-5d21-4302-b9b6-feb9a2cb68b3',
  login: 'dRt1R@XHj3\\n7p\\eT4t5Uj\\lC',
};

export const sampleWithPartialData: IUser = {
  id: '640fe64b-8576-454d-a63d-4db412d0b39e',
  login: 'Hd',
};

export const sampleWithFullData: IUser = {
  id: '3020ff1c-5c16-4630-b8ee-9e3f63549af1',
  login: 'kbx@OR\\pFHQz',
};
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
