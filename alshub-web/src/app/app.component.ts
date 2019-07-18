import {AfterContentChecked, Component} from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements AfterContentChecked {
  title = 'alshub-web';

  constructor() {

  }

  ngAfterContentChecked(): void {
  }
}
