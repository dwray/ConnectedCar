/*
 Licensed Materials - Property of IBM
 
 © Copyright IBM Corporation 2014,2015. All Rights Reserved.
 
 This licensed material is sample code intended to aid the licensee in the development of software for the Apple iOS and OS X platforms . This sample code is  provided only for education purposes and any use of this sample code to develop software requires the licensee obtain and comply with the license terms for the appropriate Apple SDK (Developer or Enterprise edition).  Subject to the previous conditions, the licensee may use, copy, and modify the sample code in any form without payment to IBM for the purposes of developing software for the Apple iOS and OS X platforms.
 
 Notwithstanding anything to the contrary, IBM PROVIDES THE SAMPLE SOURCE CODE ON AN "AS IS" BASIS AND IBM DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, ANY IMPLIED WARRANTIES OR CONDITIONS OF MERCHANTABILITY, SATISFACTORY QUALITY, FITNESS FOR A PARTICULAR PURPOSE, TITLE, AND ANY WARRANTY OR CONDITION OF NON-INFRINGEMENT. IBM SHALL NOT BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY OR ECONOMIC CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OR OPERATION OF THE SAMPLE SOURCE CODE. IBM SHALL NOT BE LIABLE FOR LOSS OF, OR DAMAGE TO, DATA, OR FOR LOST PROFITS, BUSINESS REVENUE, GOODWILL, OR ANTICIPATED SAVINGS. IBM HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS OR MODIFICATIONS TO THE SAMPLE SOURCE CODE.
 */

// David Wray - Modifications for Solace Connected Car Demo © Copyright 2018

//
//  LoginViewController.m
//  IoTstarter
//

#import "LoginViewController.h"
#import "AppDelegate.h"

@interface LoginViewController ()

@property (strong, nonatomic) IBOutlet UIButton *activateButton;

@property (strong, nonatomic) IBOutlet UILabel *connected;
@property (strong, nonatomic) IBOutlet UITextField *vinField;

@property (strong, nonatomic) IBOutlet UITextField *routerAddressField;
@property (strong, nonatomic) IBOutlet UITextField *passwordField;
@property (strong, nonatomic) IBOutlet UITextField *userNameField;

@property (strong, nonatomic) IBOutlet UIButton *showPassword;

@property (strong, nonatomic) IBOutlet UISwitch *accelSwitch;

@end

@implementation LoginViewController

- (id)initWithCoder:(NSCoder *)aDecoder
{
    self = [super initWithCoder:aDecoder];
    if (self)
    {
        AppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
        appDelegate.loginViewController = self;
    }
    return self;
}

/*************************************************************************
 * View related methods
 *************************************************************************/

- (void)viewWillAppear:(BOOL)animated
{
    [self updateViewLabels];
    AppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
    appDelegate.currentView = LOGIN;
    
    self.passwordField.secureTextEntry = YES;
    self.passwordField.delegate = self;
    self.userNameField.delegate = self;
    self.routerAddressField.delegate = self;
    self.vinField.delegate = self;
    
    self.vinField.placeholder = IOTVINPlaceholder;
    self.routerAddressField.placeholder = IOTRoutAddrPlaceholder;
    self.userNameField.placeholder = IOTUnamePlaceholder;
    self.passwordField.placeholder = IOTPwdPlaceholder;
    self.vinField.text = IOTVINDefault;
    self.routerAddressField.text = IOTRoutAddrDefault;
    self.userNameField.text = IOTUnameDefault;
    self.passwordField.text = IOTPwdDefault;
    
    if (![appDelegate.routerAddress isEqualToString:@""])
    {
        self.routerAddressField.text = appDelegate.routerAddress;
    }
    if (![appDelegate.routerAddress isEqualToString:@""])
    {
        self.routerAddressField.text = appDelegate.routerAddress;
    }
    if (![appDelegate.userName isEqualToString:@""])
    {
        self.userNameField.text = appDelegate.userName;
    }
    if (![appDelegate.password isEqualToString:@""])
    {
        self.passwordField.text = appDelegate.password;
    }
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.passwordField.secureTextEntry = YES;
    AppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];

    self.routerAddressField.text = appDelegate.routerAddress;
    self.userNameField.text = appDelegate.userName;
    self.passwordField.text = appDelegate.password;
}

- (void)updateViewLabels
{
    NSLog(@"%s:%d entered", __func__, __LINE__);
    AppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
    [self.activateButton setEnabled:YES];
    
    if (appDelegate.isConnected)
    {
        // If device is connected, then it is already registered and all values were
        // already set.
        [self.activateButton setTitle:IOTDeactivateLabel forState:UIControlStateNormal];
        self.connected.text = YES_STRING;
    }
    else
    {
        // If device is not connected, it may or may not be registered.
        [self.activateButton setTitle:IOTActivateLabel forState:UIControlStateNormal];
        self.connected.text = NO_STRING;
    }
    [self.showPassword setTitle:IOTPasswordLabel forState:UIControlStateNormal];
}

/*************************************************************************
 * IBAction methods
 *************************************************************************/

- (IBAction)showPassword:(id)sender
{
    NSLog(@"%s:%d entered", __func__, __LINE__);
    if ([self.showPassword.currentTitle isEqualToString:IOTPasswordLabel])
    {
        self.passwordField.secureTextEntry = NO;
        [self.showPassword setTitle:IOTHidePwdLabel forState:UIControlStateNormal];
    }
    else
    {
        self.passwordField.secureTextEntry = YES;
        [self.showPassword setTitle:IOTPasswordLabel forState:UIControlStateNormal];
    }
}

- (IBAction)activateSensor:(id)sender
{
    NSLog(@"%s:%d entered", __func__, __LINE__);
    AppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
    [self.activateButton setEnabled:NO];
    appDelegate.hasCrashed = FALSE;
    appDelegate.publishCount = 0;
    appDelegate.receiveCount = 0;
    CMAcceleration accel;
    accel.x = 0.0;
    accel.y = 0.0;
    accel.z = 0.0;
    [appDelegate updateAccelerationData:accel];
    [appDelegate.carDemoControllerViewController updateAccelLabels];
    
    [appDelegate storePropertiesToArchive];
    
    if ([self.activateButton.titleLabel.text isEqualToString:IOTActivateLabel])
    {
        // Start MQTT logic
        Messenger *messenger = [Messenger sharedMessenger];
        NSString *serverAddress;
        NSString *clientID;
        
        appDelegate.routerAddress = [NSString stringWithFormat:@"%@", self.routerAddressField.text];
        appDelegate.userName = [NSString stringWithFormat:@"%@", self.userNameField.text];
        appDelegate.password = [NSString stringWithFormat:@"%@", self.passwordField.text];
        appDelegate.VIN = [NSString stringWithFormat:@"%@", self.vinField.text];
        
        appDelegate.sensorFrequency = IOTSensorFreqDefault;
        serverAddress = appDelegate.routerAddress;
        clientID = [NSString stringWithFormat:IOTClientID, appDelegate.routerAddress, IOTDeviceType, appDelegate.VIN];
        
#ifdef USE_LOCAL_NOTIFICATIONS
        // Run the MQTT Connection in a background task so that it continues processing messages
        // while the application is running in the background.
        appDelegate.bgTask = [[UIApplication sharedApplication] beginBackgroundTaskWithName:@"iotbgtask" expirationHandler:^{
            [[UIApplication sharedApplication] endBackgroundTask:appDelegate.bgTask];
            appDelegate.bgTask = UIBackgroundTaskInvalid;
        }];
#endif
        [messenger connectWithHost:serverAddress port:IOTServerPort clientId:clientID];
    }
    else if ([self.activateButton.titleLabel.text isEqualToString:IOTDeactivateLabel])
    {
        // Stop MQTT logic
        Messenger *messenger = [Messenger sharedMessenger];
        
        [messenger disconnect];
    }
}

- (IBAction)rightViewChangePressed:(id)sender
{
    AppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
    [appDelegate switchToIoTView];
}

- (IBAction)leftViewChangePressed:(id)sender
{
    AppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
    [appDelegate switchToTrackingView];
}

//TODO this whole routine can go from the controller (but we need it for the sensor)
- (IBAction)toggleAccel:(id)sender
{
    AppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
    appDelegate.isAccelEnabled = [self.accelSwitch isOn];
    if (appDelegate.isConnected && appDelegate.isAccelEnabled)
    {
        [appDelegate startMotionManager];
    }
    else if (appDelegate.isConnected && !appDelegate.isAccelEnabled)
    {
        [appDelegate stopMotionManager];
    }
}

#pragma mark UITextFieldDelegate

- (BOOL)textFieldShouldReturn:(UITextField *)text
{
    [text resignFirstResponder];
    return YES;
}

/*************************************************************************
 * Other standard iOS methods
 *************************************************************************/

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    ProfilesTableViewController *tableView = [segue destinationViewController];
    tableView.currentRouterAddress = [NSString stringWithString:self.routerAddressField.text];
    tableView.currentUserName = [NSString stringWithString:self.userNameField.text];
    tableView.currentPassword = [NSString stringWithString:self.passwordField.text];
}

@end
