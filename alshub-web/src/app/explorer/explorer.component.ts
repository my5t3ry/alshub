import {Component, OnInit} from '@angular/core';
import {Observable} from "rxjs";
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Router} from "@angular/router";
import {NotifierService} from "angular-notifier";
import {RequestInterceptorService} from "../../request-interceptor.service";

@Component({
  selector: 'app-explorer',
  templateUrl: './explorer.component.html',
  styleUrls: ['./explorer.component.scss']
})
export class ExplorerComponent implements OnInit {

  endpoint = 'http://localhost/api/explorer';

  items: any;
  path: any;

  constructor(private http: HttpClient, private router: Router, private notifierService: NotifierService) {
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
    return this.http.get<any>(this.endpoint + "/", RequestInterceptorService.httpOptions).pipe();
  }

  selectDir(item: any) {
    this.http.post(this.endpoint + '/set-path/', {path: item.absolutePath})
      .subscribe(
        data => {
          this.refresh(data);
        });
  }

  refresh(data: any) {
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
          // @ts-ignore
          this.router.navigateByUrl("/project-detail-edit/" + data.id)
        });
  }
}
