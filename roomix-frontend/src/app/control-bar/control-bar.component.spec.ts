import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { IonicModule } from '@ionic/angular';

import { ControlBarComponent } from './control-bar.component';

describe('ControlBarComponent', () => {
  let component: ControlBarComponent;
  let fixture: ComponentFixture<ControlBarComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ControlBarComponent ],
      imports: [IonicModule.forRoot()]
    }).compileComponents();

    fixture = TestBed.createComponent(ControlBarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
