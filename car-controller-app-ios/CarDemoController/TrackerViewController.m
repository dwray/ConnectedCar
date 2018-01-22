//
//  TrackerViewController.m
//  CarDemoController
//
//  Created by David Wray on 16/01/2018.
//  Copyright © 2018 Solace Corporation. All rights reserved.
//

#import "TrackerViewController.h"
#import "AppDelegate.h"

@interface TrackerViewController()
@property (strong, nonatomic) IBOutlet MKMapView *mapView;

@end

@implementation TrackerViewController

- (id)initWithCoder:(NSCoder *)aDecoder
{
    self = [super initWithCoder:aDecoder];
    if (self)
    {
        AppDelegate *appDelegate = (AppDelegate *) [[UIApplication sharedApplication] delegate];
        appDelegate.trackerViewController = self;
    }
    return self;
}

/*************************************************************************
 * View related methods
 *************************************************************************/

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.
}

- (void)handleLocationDisplay {
    AppDelegate *appDelegate = (AppDelegate *) [[UIApplication sharedApplication] delegate];
    // Set map region
    if (appDelegate.latestLocation.latitude != 0.0f) {
        CLLocationCoordinate2D location = {.latitude = appDelegate.latestLocation.latitude, .longitude = appDelegate.latestLocation.longitude};
        MKCoordinateSpan cspan = {.latitudeDelta = 0.008f, .longitudeDelta = 0.008f};
        MKCoordinateRegion cregion = {location, cspan};
        [_mapView setRegion:cregion];
        _mapView.showsScale = TRUE;
        _mapView.showsCompass = TRUE;
        
        // add pin
        if (_pAnnotation == nil) {
            _pAnnotation = [[MKPointAnnotation alloc]init];
            [_mapView addAnnotation:_pAnnotation];
        }
        [_pAnnotation setCoordinate:location];
        [_mapView setCenterCoordinate:location animated:TRUE];
        if (appDelegate.hasCrashed) {
            [_pAnnotation setTitle:@"Crash Location"];
        } else {
            [_pAnnotation setTitle:@"Car Location"];
        }
    }
}
- (IBAction)rightViewChangePressed:(id)sender {
    AppDelegate *appDelegate =(AppDelegate *) [[UIApplication sharedApplication] delegate];
       [appDelegate switchToLogView];
    }
- (IBAction)leftViewChangePressed:(id)sender {
    AppDelegate *appDelegate =(AppDelegate *) [[UIApplication sharedApplication] delegate];
    [appDelegate switchToIoTView];
}

- (void)viewWillAppear:(BOOL)animated
{
    AppDelegate *appDelegate = (AppDelegate *) [[UIApplication sharedApplication] delegate];
    appDelegate.currentView = TRACKER;
    [self handleLocationDisplay];
}

- (void) updatePinLocation {
    if (_pAnnotation == nil) {
        // map not ready
        return;
    }
    [self handleLocationDisplay];
}

@end
