import {BrowserModule} from '@angular/platform-browser';
import {APP_INITIALIZER, NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {ExplorerComponent} from './explorer/explorer.component';
import {HTTP_INTERCEPTORS, HttpClient, HttpClientModule, HttpHandler} from "@angular/common/http";
import {initializer} from "../app-init";
import {SpinnerService} from "../spinnner.service";
import {RequestInterceptorService} from "../request-interceptor.service";
import {KeycloakService, KeycloakAngularModule} from 'keycloak-angular';
import {NotifierModule, NotifierService} from "angular-notifier";
import {NavComponent} from './nav/nav.component';
import {SpinnerComponent} from "./spinner/spinner.component";
import {MyProjectsComponent} from './my-projects/my-projects.component';
import {ProjectDetailComponent} from './project-detail/project-detail.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {TabsModule} from "ngx-bootstrap";
import {ErrorComponent} from "./error/error.component";

declare var fs: any;

@NgModule({
  declarations: [
    AppComponent,
    ExplorerComponent,
    SpinnerComponent,
    NavComponent,
    ErrorComponent,
    MyProjectsComponent,
    ProjectDetailComponent
  ],
  imports: [
    BrowserModule,
    TabsModule.forRoot(),
    KeycloakAngularModule,
    AppRoutingModule,
    HttpClientModule,
    NotifierModule.withConfig({
      position: {
        horizontal: {
          position: 'right',
          distance: 12
        },
        vertical: {
          position: 'top',
          distance: 12,
          gap: 10
        }
      },
      behaviour: {
        autoHide: 5000
      }
    }),
    BrowserAnimationsModule],

  providers: [HttpClient,
    {provide: HTTP_INTERCEPTORS, useClass: RequestInterceptorService, multi: true},
    // { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptorService, multi: true },
    SpinnerService,
    NotifierService,
    {
      provide: APP_INITIALIZER,
      useFactory: initializer,
      multi: true,
      deps: [KeycloakService],
    }],
  bootstrap: [AppComponent]
})
export class AppModule {
}
