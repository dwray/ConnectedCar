/*
 Licensed Materials - Property of IBM
 
 © Copyright IBM Corporation 2014,2015. All Rights Reserved.
 
 This licensed material is sample code intended to aid the licensee in the development of software for the Apple iOS and OS X platforms . This sample code is  provided only for education purposes and any use of this sample code to develop software requires the licensee obtain and comply with the license terms for the appropriate Apple SDK (Developer or Enterprise edition).  Subject to the previous conditions, the licensee may use, copy, and modify the sample code in any form without payment to IBM for the purposes of developing software for the Apple iOS and OS X platforms.
 
 Notwithstanding anything to the contrary, IBM PROVIDES THE SAMPLE SOURCE CODE ON AN "AS IS" BASIS AND IBM DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, ANY IMPLIED WARRANTIES OR CONDITIONS OF MERCHANTABILITY, SATISFACTORY QUALITY, FITNESS FOR A PARTICULAR PURPOSE, TITLE, AND ANY WARRANTY OR CONDITION OF NON-INFRINGEMENT. IBM SHALL NOT BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY OR ECONOMIC CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OR OPERATION OF THE SAMPLE SOURCE CODE. IBM SHALL NOT BE LIABLE FOR LOSS OF, OR DAMAGE TO, DATA, OR FOR LOST PROFITS, BUSINESS REVENUE, GOODWILL, OR ANTICIPATED SAVINGS. IBM HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS OR MODIFICATIONS TO THE SAMPLE SOURCE CODE.
 */

// David Wray - Modifications for Solace Connected Car Demo © Copyright 2018

//
//  Constants.m
//  IoTstarter
//

#import "Constants.h"

@implementation Constants

/** File name for storing application properties on device */
NSString * const IOTArchiveFileName   = @"/IoTstarter.archive";
/** Application property names */
NSString * const IOTPassword         = @"authtoken";
NSString * const IOTUserName          = @"deviceid";
NSString * const IOTRouterAddress      = @"organization";

// IoT API Constants
NSString * const IOTDeviceType        = @"iPhone";

// MQTT Constants

NSString * const IOTServerAddress     = @"%@.messaging.internetofthings.ibmcloud.com";
int        const IOTServerPort        = 1883;
// d:org:type:VIN
NSString * const IOTClientID          = @"d:%@:%@:%@";
NSString * const IOTEventTopic        = @"cardemo/%@/evt/%@/fmt/%@";
NSString * const IOTCommandTopic      = @"cardemo/%@/cmd/%@/fmt/%@";

// IoT Events and Commands
NSString * const IOTAccelEvent        = @"accel";
NSString * const IOTColorEvent        = @"color";
NSString * const IOTTouchMoveEvent    = @"touchmove";
NSString * const IOTSwipeEvent        = @"swipe";
NSString * const IOTLightEvent        = @"light";
NSString * const IOTTextEvent         = @"text";
NSString * const IOTAlertEvent        = @"alert";
NSString * const IOTCrashEvent        = @"crash";
NSString * const IOTTrackingEvent        = @"track";
NSString * const IOTStatusEvent       = @"status";

NSString * const IOTLightCommand         = @"light";
NSString * const IOTColorCommand         = @"color";
NSString * const IOTHornCommand          = @"horn";
NSString * const IOTLockCommand          = @"lock";
NSString * const IOTLockCommand_locked   = @"locked";
NSString * const IOTLockCommand_unlocked = @"unlocked";
NSString * const IOTEngineCommand        = @"engine";

// Login View button and placeholder text
NSString * const IOTRoutAddrPlaceholder    = @"Router Address";
NSString * const IOTRoutAddrDefault    = @"london.solace.com";
NSString * const IOTUnamePlaceholder = @"User Name";
NSString * const IOTUnameDefault = @"default";
NSString * const IOTPwdPlaceholder   = @"Password";
NSString * const IOTPwdDefault   = @"default";
NSString * const IOTVINPlaceholder   = @"Vehicle VIN";
NSString * const IOTVINDefault   = @"1234567890123456789";

NSString * const IOTPasswordLabel    = @"Show Password";
NSString * const IOTHidePwdLabel    = @"Hide Password";
NSString * const IOTActivateLabel     = @"Activate Controller";
NSString * const IOTDeactivateLabel   = @"Deactivate Controller";

double const IOTSensorFreqDefault     = 1.0;
double const IOTSensorFreqFast        = 0.2;

// Extra strings
NSString * const YES_STRING           = @"Yes";
NSString * const NO_STRING            = @"No";
NSString * const CANCEL_STRING        = @"Cancel";
NSString * const SUBMIT_STRING        = @"Submit";
NSString * const OK_STRING            = @"OK";

// JSON Fields
NSString * const JSON_TEXT            = @"text";
NSString * const JSON_HORN            = @"horn";
NSString * const JSON_LOCK            = @"lock";
NSString * const JSON_ENGINE          = @"engine";
NSString * const JSON_LIGHTS          = @"lights";

NSString * const JSON_COLOR_R         = @"r";
NSString * const JSON_COLOR_G         = @"g";
NSString * const JSON_COLOR_B         = @"b";
NSString * const JSON_ALPHA           = @"alpha";

NSString * const JSON_ROLL            = @"roll";
NSString * const JSON_PITCH           = @"pitch";
NSString * const JSON_YAW             = @"yaw";
NSString * const JSON_ACCEL_X         = @"acceleration_x";
NSString * const JSON_ACCEL_Y         = @"acceleration_y";
NSString * const JSON_ACCEL_Z         = @"acceleration_z";
NSString * const JSON_LAT             = @"latitude";
NSString * const JSON_LON             = @"longitude";


@end

/*
 server = <org>.messaging.internetofthings.ibmcloud.com:1883
 username = "use-token-auth"
 password = token returned from registering device
 clientid = d:<org>:iPhone:<deviceid>
 
 topic = cardemo/evt/<event-type-id>/fmt/<format>
    <event-type-id> = ???
    <format> = json?
 
 topic = cardemo/cmd/<command-type-id>/fmt/<format>
 <command-type-id> = ???
 <format> = json?
 
 QoS=0
 No retained support
 
 JSON payload, top level = d
 {
 "d": {
 "name1": "stringvalue",
 "name2": intvalue,
 ...
 }
 }
 */

