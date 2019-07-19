import { Component, OnInit } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {SpinnerService} from "../../spinnner.service";

@Component({
  selector: 'app-spinner',
  templateUrl: './spinner.component.html',
  styleUrls: ['./spinner.component.scss']
})
export class SpinnerComponent implements OnInit {

  public visibility: boolean;

  constructor(private _http: HttpClient, private spinnerService: SpinnerService) { }

  ngOnInit() {
    this.spinnerService.visibility.subscribe(state => {
      this.visibility = state;
    });
    this.spinnerService.notVisibility.subscribe(state => {
      this.visibility = state;
    });
  }
}
