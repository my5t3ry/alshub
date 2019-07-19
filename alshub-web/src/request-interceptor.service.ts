import {Injectable} from '@angular/core';
import {HttpErrorResponse, HttpEvent, HttpHandler, HttpHeaders, HttpInterceptor, HttpRequest, HttpResponse} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError, finalize, tap} from 'rxjs/operators';
import {SpinnerService} from './spinnner.service';
import {NotifierService} from 'angular-notifier';
import {Router} from '@angular/router';

@Injectable()
export class RequestInterceptorService implements HttpInterceptor {

  public static httpOptions = {
    headers: new HttpHeaders({'Content-Type': 'application/json', 'Accept': 'application/json'})
  };

  constructor(private spinner: SpinnerService, private notifierService: NotifierService, private router: Router) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    this.spinner.visibility.emit(true);
    return next.handle(req).pipe(
      tap(
        event => {
          status = '';
          if (event instanceof HttpResponse) {
            this.handleMessage(event)
          }
        }
      ),
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
    )
  }

  private handleMessage(req: HttpResponse<any>) {
    if (req.headers.get("message")) {
      this.notifierService.notify(req.headers.get("message-type"), req.headers.get("message"));
    }
  }
}
