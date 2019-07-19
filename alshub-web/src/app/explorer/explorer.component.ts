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
  path: any;

  constructor(private http: HttpClient) {
  }

  ngOnInit() {
    this.fetchItems();
  }

  fetchItems() {
    this.getClients().subscribe(data => {
      this.refresh(data);

    });
  }

  getClients(): Observable<any> {
    return this.http.get<any>(this.endpoint + "/", this.httpOptions).pipe();
  }

  selectDir(item: any) {
    this.http.post(this.endpoint + '/set-path/', {path: item.absolutePath})
      .subscribe(
        data => {
          this.refresh(data);
        });
  }

  refresh(data: Object) {
    this.items = data.items;
    this.path = data.path;
  }

  selectParentDir() {
    this.http.post(this.endpoint + '/set-parent/', null)
      .subscribe(
        data => {
          this.refresh(data);
        });
  }

  addProject(item: any) {
    this.http.post(this.endpoint + '/add-project/', {path: item.absolutePath})
      .subscribe(
        data => {
          console.log(data)
        });
  }
}
