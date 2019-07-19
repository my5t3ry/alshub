import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {ExplorerComponent} from "./explorer/explorer.component";
import {AppAuthGuard} from "../app.authguard";
import {MyProjectsComponent} from "./my-projects/my-projects.component";
import {ProjectDetailComponent} from "./project-detail/project-detail.component";
import {ErrorComponent} from "./error/error.component";

const routes: Routes = [{
  path: '',
  component: MyProjectsComponent,
  canActivate: [AppAuthGuard],
  data: {roles: ['alshub']}
}, {
  path: 'explorer',
  component: ExplorerComponent,
  canActivate: [AppAuthGuard],
  data: {roles: ['alshub']}
},
  {
    path: 'project-detail/:projectId', component: ProjectDetailComponent,
    canActivate: [AppAuthGuard],
    data: {roles: ['alshub']}
  }, {
    path: 'error/:errorCode', component: ErrorComponent,
    canActivate: [AppAuthGuard],
    data: {roles: ['alshub']}
  },
  {
    path: 'error/:errorCode/:error', component: ErrorComponent,
    canActivate: [AppAuthGuard],
    data: {roles: ['alshub']}
  }];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
  providers: [AppAuthGuard]

})
export class AppRoutingModule {
}
