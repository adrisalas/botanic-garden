import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { GeneralStoreService } from './general-store.service';
import { Observable } from 'rxjs';
import { News } from '../model/news_interface';
import { environment } from '../../environments/environment.development';

@Injectable({
  providedIn: 'root'
})
export class NewsService {

  constructor(private _httpClient: HttpClient,
    private generalData: GeneralStoreService) { }

  getAllNews(): Observable<News[]> {
    return this._httpClient.get<News[]>(environment.urlNews);
  }

  createNews(news: News): Observable<any> {
    delete news.id;
    var headers = new HttpHeaders({
      "Content-Type": "application/json",
      "Accept": "application/json",
      "Authorization": this.generalData.getAuthorizationHeader()
    });

    return this._httpClient.post<any>(environment.urlNews, JSON.stringify(news), {
      headers: headers
    });
  }

  updateNews(news: News): Observable<any> {
    var headers = new HttpHeaders({
      "Content-Type": "application/json",
      "Accept": "application/json",
      "Authorization": this.generalData.getAuthorizationHeader()
    });

    return this._httpClient.put<any>(environment.urlNews + "/" + news.id, JSON.stringify(news), {
      headers: headers
    });
  }

  deleteNews(id: number): Observable<any> {
    var headers = new HttpHeaders({
      "Authorization": this.generalData.getAuthorizationHeader()
    });

    return this._httpClient.delete<any>(environment.urlNews + "/" + id, {
      headers
    });
  }
}
