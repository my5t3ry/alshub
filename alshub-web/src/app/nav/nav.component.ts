import {Component, OnInit} from '@angular/core';
import {KeycloakService} from "keycloak-angular";

@Component({
  selector: 'app-nav',
  templateUrl: './nav.component.html',
  styleUrls: ['./nav.component.scss']
})
export class NavComponent implements OnInit {

  constructor(private kcService: KeycloakService) {
  }

  ngOnInit() {
  }


  logout() {
    this.kcService.logout("http://alshub.local")
  }
}
