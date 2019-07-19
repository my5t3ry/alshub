import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {ExplorerComponent} from "./explorer/explorer.component";
import {AppAuthGuard} from "../app.authguard";
import {MyProjectsComponent} from "./my-projects/my-projects.component";

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
}];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
  providers: [AppAuthGuard]

})
export class AppRoutingModule {
}
