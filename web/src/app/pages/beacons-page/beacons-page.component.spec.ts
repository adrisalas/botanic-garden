import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BeaconsPageComponent } from './beacons-page.component';

describe('BeaconsPageComponent', () => {
  let component: BeaconsPageComponent;
  let fixture: ComponentFixture<BeaconsPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BeaconsPageComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(BeaconsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
