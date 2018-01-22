/*
 Licensed Materials - Property of IBM
 
 © Copyright IBM Corporation 2014,2015. All Rights Reserved.
 
 This licensed material is sample code intended to aid the licensee in the development of software for the Apple iOS and OS X platforms . This sample code is  provided only for education purposes and any use of this sample code to develop software requires the licensee obtain and comply with the license terms for the appropriate Apple SDK (Developer or Enterprise edition).  Subject to the previous conditions, the licensee may use, copy, and modify the sample code in any form without payment to IBM for the purposes of developing software for the Apple iOS and OS X platforms.
 
 Notwithstanding anything to the contrary, IBM PROVIDES THE SAMPLE SOURCE CODE ON AN "AS IS" BASIS AND IBM DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, ANY IMPLIED WARRANTIES OR CONDITIONS OF MERCHANTABILITY, SATISFACTORY QUALITY, FITNESS FOR A PARTICULAR PURPOSE, TITLE, AND ANY WARRANTY OR CONDITION OF NON-INFRINGEMENT. IBM SHALL NOT BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY OR ECONOMIC CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OR OPERATION OF THE SAMPLE SOURCE CODE. IBM SHALL NOT BE LIABLE FOR LOSS OF, OR DAMAGE TO, DATA, OR FOR LOST PROFITS, BUSINESS REVENUE, GOODWILL, OR ANTICIPATED SAVINGS. IBM HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS OR MODIFICATIONS TO THE SAMPLE SOURCE CODE.
 */

// David Wray - Modifications for Solace Connected Car Demo © Copyright 2018

//
//  ViewController.m
//  IoTstarter
//

#import "CarDemoControllerViewController.h"
#import "AppDelegate.h"
#import <AVFoundation/AVFoundation.h>

@interface CarDemoControllerViewController ()
{
    NSArray *_ambianceData;
    AVAudioPlayer *_avAudioPlayer;
}
@property (strong, nonatomic) IBOutlet UILabel *deviceId;

@property (strong, nonatomic) IBOutlet UILabel *accelX;
@property (strong, nonatomic) IBOutlet UILabel *accelY;
@property (strong, nonatomic) IBOutlet UILabel *accelZ;

@property (strong, nonatomic) IBOutlet UILabel *messagesPublished;
@property (strong, nonatomic) IBOutlet UILabel *messagesReceived;

//@property (strong, nonatomic) IBOutlet DrawingView *colorView;
@property (strong, nonatomic) IBOutlet UIView *colorView;
@property (strong, nonatomic) IBOutlet UIImageView *borderImage;

@property (strong, nonatomic) IBOutlet UIPickerView *ambiancePicker;
@property (strong, nonatomic) IBOutlet UIButton *hornButton;
@property (strong, nonatomic) IBOutlet UIButton *startButton;
@property (strong, nonatomic) IBOutlet UISwitch *lightsSwitch;
@property (strong, nonatomic) IBOutlet UISwitch *lockSwitch;

@end

@implementation CarDemoControllerViewController
@synthesize ambiancePicker;

- (id)initWithCoder:(NSCoder *)aDecoder
{
    self = [super initWithCoder:aDecoder];
    if (self)
    {
        AppDelegate *appDelegate = (AppDelegate *) [[UIApplication sharedApplication] delegate];
        appDelegate.carDemoControllerViewController = self;
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
    ambiancePicker.showsSelectionIndicator = TRUE;
    _ambianceData = @[@"Red", @"Green", @"Blue"];
    // Connect data
    self.ambiancePicker.dataSource = self;
    self.ambiancePicker.delegate = self;
    [ambiancePicker selectRow:(1) inComponent:(0) animated:(false)];
    
    // Construct URL to sound file
    NSString *path = [NSString stringWithFormat:@"%@/crash.mp3", [[NSBundle mainBundle] resourcePath]];
    NSURL *soundUrl = [NSURL fileURLWithPath:path];
    
    // Create audio player object and initialize with URL to sound
    _avAudioPlayer = [[AVAudioPlayer alloc] initWithContentsOfURL:soundUrl error:nil];
}

- (void)viewWillAppear:(BOOL)animated
{
    [self updateViewLabels];
    [self updateMessageCounts];
    AppDelegate *appDelegate = (AppDelegate *) [[UIApplication sharedApplication] delegate];
    appDelegate.currentView = CONTROLLER;
    
    int r = self.borderImage.layer.frame.size.height;
    self.borderImage.layer.cornerRadius = r/2;
    self.borderImage.layer.masksToBounds = YES;
    self.borderImage.layer.borderWidth = 2;
    self.borderImage.layer.borderColor = [UIColor whiteColor].CGColor;
}

- (void) playCrashSound {
    [_avAudioPlayer play];
}
- (void)updateViewLabels
{
    NSLog(@"%s:%d entered", __func__, __LINE__);
    AppDelegate *appDelegate =(AppDelegate *) [[UIApplication sharedApplication] delegate];
//    self.deviceId.text = appDelegate.userName;
    self.deviceId.text = appDelegate.VIN;
}

- (void)updateMessageCounts
{
    NSLog(@"%s:%d entered", __func__, __LINE__);
    AppDelegate *appDelegate =(AppDelegate *) [[UIApplication sharedApplication] delegate];
    self.messagesPublished.text = [NSString stringWithFormat:@"%zd", appDelegate.publishCount];
    self.messagesReceived.text = [NSString stringWithFormat:@"%zd", appDelegate.receiveCount];
}

- (void)updateAccelLabels
{
    AppDelegate *appDelegate =(AppDelegate *) [[UIApplication sharedApplication] delegate];
    CMAcceleration accel = appDelegate.latestAcceleration;
    self.accelX.text = [NSString stringWithFormat:@"x: %f", accel.x];
    self.accelY.text = [NSString stringWithFormat:@"y: %f", accel.y];
    self.accelZ.text = [NSString stringWithFormat:@"z: %f", accel.z];
}

- (void)updateBackgroundColor:(UIColor *)color
{
    NSLog(@"%s:%d entered", __func__, __LINE__);
    [self.colorView setBackgroundColor:color];
    //NSLog(@"Background color updated");
}


/*************************************************************************
 * IBAction methods
 *************************************************************************/
- (IBAction)startButtonPressed:(id)sender {

    AppDelegate *appDelegate =(AppDelegate *) [[UIApplication sharedApplication] delegate];
    
    NSString *messageData = [MessageFactory createCommandMessage:JSON_ENGINE withValue:@"start"];
    
    [appDelegate publishCommand:messageData command:IOTEngineCommand QoS:0 VIN:appDelegate.VIN];
}
- (IBAction)hornButtonPressed:(id)sender {
    
    AppDelegate *appDelegate =(AppDelegate *) [[UIApplication sharedApplication] delegate];
    
    NSString *messageData = [MessageFactory createCommandMessage:JSON_HORN withValue:@"sound"];
    
    [appDelegate publishCommand:messageData command:IOTHornCommand QoS:0 VIN:appDelegate.VIN];
}
- (IBAction)lightsToggled:(id)sender {
    NSString *lightsAre = _lightsSwitch.on ? @"on" : @"off";
    
    AppDelegate *appDelegate =(AppDelegate *) [[UIApplication sharedApplication] delegate];
    
    NSString *messageData = [MessageFactory createCommandMessage:JSON_LIGHTS withValue:lightsAre];

    [appDelegate publishCommand:messageData command:IOTLightCommand QoS:0 VIN:appDelegate.VIN];
}
- (IBAction)lockToggled:(id)sender {
    NSString *lockIs = _lockSwitch.on ? IOTLockCommand_locked : IOTLockCommand_unlocked;
    
    AppDelegate *appDelegate =(AppDelegate *) [[UIApplication sharedApplication] delegate];
    
    NSString *messageData = [MessageFactory createCommandMessage:JSON_LOCK withValue:lockIs];
    
    [appDelegate publishCommand:messageData command:IOTLockCommand QoS:0 VIN:appDelegate.VIN];
}

- (IBAction)rightViewChangePressed:(id)sender
{
    AppDelegate *appDelegate =(AppDelegate *) [[UIApplication sharedApplication] delegate];
    [appDelegate switchToTrackingView];
}

- (IBAction)leftViewChangePressed:(id)sender
{
    AppDelegate *appDelegate =(AppDelegate *) [[UIApplication sharedApplication] delegate];
    [appDelegate switchToLoginView];
}

/*************************************************************************
 * Alert View Handler
 *************************************************************************/

- (void)   alertView:(UIAlertView *)alertView
clickedButtonAtIndex:(NSInteger)buttonIndex
{
    NSLog(@"%s:%d entered", __func__, __LINE__);
    if ([[alertView textFieldAtIndex:0].text isEqualToString:@""] || [[alertView buttonTitleAtIndex:buttonIndex] isEqualToString:CANCEL_STRING])
    {
        // skip empty input or when cancel pressed
        return;
    }
    
    AppDelegate *appDelegate =(AppDelegate *) [[UIApplication sharedApplication] delegate];
    
    NSString *messageData = [MessageFactory createTextMessage:[alertView textFieldAtIndex:0].text];

    [appDelegate publishData:messageData event:IOTTextEvent VIN:appDelegate.VIN];
}

/*************************************************************************
 * Other standard iOS methods
 *************************************************************************/

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}
// The number of columns of data
- (NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView
{
    return 1;
}
// The number of rows of data
- (NSInteger)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component
{
    return _ambianceData.count;
}
// The data to return for the row and component (column) that's being passed in
- (NSString*)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component
{
    return _ambianceData[row];
}

- (void)pickerView:(UIPickerView *)pickerView didSelectRow:(NSInteger)row inComponent:(NSInteger)component {
    
    AppDelegate *appDelegate =(AppDelegate *) [[UIApplication sharedApplication] delegate];
    
    NSString *messageData = [MessageFactory createColourCommandMessage:_ambianceData[row]];
    
    [appDelegate publishCommand:messageData command:IOTColorEvent QoS:1 VIN:appDelegate.VIN];
}
- (IBAction)deviceId:(id)sender {
}
@end
