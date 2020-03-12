import {Info} from './info.model'
export class AsyncApiDoc {
  constructor (
    public asyncapi: string,
    public info: Info
  ) {}
}
