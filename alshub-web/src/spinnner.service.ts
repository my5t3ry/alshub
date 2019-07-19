import {Injectable, EventEmitter} from '@angular/core';

@Injectable()
export class SpinnerService {
  public visibility: EventEmitter<boolean> = new EventEmitter();
  public notVisibility: EventEmitter<boolean> = new EventEmitter();

  constructor() {
  }
}
