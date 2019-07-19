import {AfterContentChecked, Component} from '@angular/core';
import {setTheme} from "ngx-bootstrap";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements AfterContentChecked {
  title = 'alshub-web';

  constructor() {
    setTheme('bs4'); // or 'bs4'

  }

  ngAfterContentChecked(): void {
  }
}
