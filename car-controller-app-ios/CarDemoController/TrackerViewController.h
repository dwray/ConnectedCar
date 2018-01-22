//
//  TrackerViewController.h
//  CarDemoController
//
//  Created by David Wray on 16/01/2018.
//  Copyright © 2018 Solace Corporation. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <MapKit/MapKit.h>

@interface TrackerViewController : UIViewController <MKMapViewDelegate>
@property (strong, atomic) MKPointAnnotation *pAnnotation;
- (void) updatePinLocation;
@end
