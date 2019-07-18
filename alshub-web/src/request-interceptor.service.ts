import {Injectable} from '@angular/core';
import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError, finalize} from 'rxjs/operators';
import {SpinnerService} from './spinnner.service';
import {NotifierService} from 'angular-notifier';
import {Router} from '@angular/router';

@Injectable()
export class RequestInterceptorService implements HttpInterceptor {

  constructor(private spinner: SpinnerService, private notifierService: NotifierService, private router: Router) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    this.spinner.visibility.emit(true);
    return next.handle(req)
      .pipe(
        finalize(() => this.spinner.notVisibility.emit(false)), catchError((error: HttpErrorResponse) => {
          let errorMessage = '';
          if (error.error.type == 'MINOR') {
            this.notifierService.notify('error', error.error.message);
          } else {
            if (error.error instanceof Object) {
              errorMessage = 'Error Code: [' + error.status + '], Message: ' + error.error.message;
              this.router.navigate(['/error/500/' + JSON.stringify(error.error)]);
            } else {
              errorMessage = 'Error: ' + error.error;
              this.router.navigate(['/error/500/' + error.error]);
            }
          }
          return throwError(errorMessage);
        })
      );
  }
}
