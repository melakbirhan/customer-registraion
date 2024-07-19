import dayjs from 'dayjs/esm';
import { Session } from 'app/entities/enumerations/session.model';

export interface ICustomer {
  id: string;
  fullName?: string | null;
  customerId?: number | null;
  department?: string | null;
  sessionType?: keyof typeof Session | null;
  telephone?: string | null;
  date?: dayjs.Dayjs | null;
}

export type NewCustomer = Omit<ICustomer, 'id'> & { id: null };
