import {Component, OnInit} from '@angular/core';
import {Observable} from "rxjs";
import {HttpClient, HttpClientModule, HttpHeaders} from '@angular/common/http';

@Component({
  selector: 'app-explorer',
  templateUrl: './explorer.component.html',
  styleUrls: ['./explorer.component.scss']
})
export class ExplorerComponent implements OnInit {

  private endpoint = 'http://localhost:8090/api/explorer';
  private httpOptions = {
    headers: new HttpHeaders({'Content-Type': 'application/json', 'Accept': 'application/json'})
  };
  private items: any;

  constructor(private http: HttpClient) {
  }

  ngOnInit() {
    this.refresh();
  }

  refresh() {
    this.getClients().subscribe(value => {
      this.items = value;
      console.log(this.items);
    });
  }

  getClients(): Observable<any> {
    return this.http.get<any>(this.endpoint + "/", this.httpOptions).pipe();
  }

  selectDir(item: any) {
    this.http.post(this.endpoint + '/set-path/', {path: item.absolutePath})
      .subscribe(
        data => {
          this.items = data;
        });
  }

  selectParentDir() {
    this.http.post(this.endpoint + '/set-parent/',null)
      .subscribe(
        data => {
          this.items = data;
        });
  }
}
