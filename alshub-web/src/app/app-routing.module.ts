import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {ExplorerComponent} from "./explorer/explorer.component";
import {AppAuthGuard} from "../app.authguard";

const routes: Routes = [{
  path: '',
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
