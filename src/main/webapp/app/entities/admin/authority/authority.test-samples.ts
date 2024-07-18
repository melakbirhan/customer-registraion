import { IAuthority, NewAuthority } from './authority.model';

export const sampleWithRequiredData: IAuthority = {
  name: '6397224b-ae18-41b9-a280-00f5cb5aaafc',
};

export const sampleWithPartialData: IAuthority = {
  name: '12436ccc-cf95-4139-ad3d-9b44c4bacf44',
};

export const sampleWithFullData: IAuthority = {
  name: '8a836b1f-0076-4902-b21d-86603f5475a0',
};

export const sampleWithNewData: NewAuthority = {
  name: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
