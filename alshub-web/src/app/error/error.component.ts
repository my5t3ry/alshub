import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-error',
  templateUrl: './error.component.html',
  styleUrls: ['./error.component.scss']
})
export class ErrorComponent implements OnInit {
  error: any;

  constructor(private route: ActivatedRoute) {
  }
  errorCode = 0;

  messages = {
    0: 'unbekannter Fehler',
    500: 'Backend Fehler',
    405: 'Zugriff verweigert.'
  };

  ngOnInit() {
    this.route.params.subscribe((params: any) => {
      if (params.errorCode) {
        this.errorCode = params.errorCode;
      }
      if (params.error) {
        this.error = JSON.parse(params.error);
      }
    });
  }

  getError(): string {
    return this.error;
  }
}
