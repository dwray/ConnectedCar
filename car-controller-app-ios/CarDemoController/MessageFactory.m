/*
 Licensed Materials - Property of IBM
 
 © Copyright IBM Corporation 2014,2015. All Rights Reserved.
 
 This licensed material is sample code intended to aid the licensee in the development of software for the Apple iOS and OS X platforms . This sample code is  provided only for education purposes and any use of this sample code to develop software requires the licensee obtain and comply with the license terms for the appropriate Apple SDK (Developer or Enterprise edition).  Subject to the previous conditions, the licensee may use, copy, and modify the sample code in any form without payment to IBM for the purposes of developing software for the Apple iOS and OS X platforms.
 
 Notwithstanding anything to the contrary, IBM PROVIDES THE SAMPLE SOURCE CODE ON AN "AS IS" BASIS AND IBM DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, ANY IMPLIED WARRANTIES OR CONDITIONS OF MERCHANTABILITY, SATISFACTORY QUALITY, FITNESS FOR A PARTICULAR PURPOSE, TITLE, AND ANY WARRANTY OR CONDITION OF NON-INFRINGEMENT. IBM SHALL NOT BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY OR ECONOMIC CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OR OPERATION OF THE SAMPLE SOURCE CODE. IBM SHALL NOT BE LIABLE FOR LOSS OF, OR DAMAGE TO, DATA, OR FOR LOST PROFITS, BUSINESS REVENUE, GOODWILL, OR ANTICIPATED SAVINGS. IBM HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS OR MODIFICATIONS TO THE SAMPLE SOURCE CODE.
 */

// David Wray - Modifications for Solace Connected Car Demo © Copyright 2018

//
//  MessageFactory.m
//  IoTstarter
//

#import "AppDelegate.h"
#import "MessageFactory.h"
#import "Constants.h"

/**
 */

@implementation MessageFactory

/** 
 *  @param
 *  @return messageString
 */
+ (NSString *)createAccelMessage:(double)accel_x
                         accel_y:(double)accel_y
                         accel_z:(double)accel_z
                            roll:(double)roll
                           pitch:(double)pitch
                             yaw:(double)yaw
                             lat:(double)lat
                             lon:(double)lon
{
    NSDictionary *messageDictionary = @{
                                        @"d": @{
                                                JSON_ACCEL_X: @(accel_x),
                                                JSON_ACCEL_Y: @(accel_y),
                                                JSON_ACCEL_Z: @(accel_z),
                                                JSON_ROLL: @(roll),
                                                JSON_PITCH: @(pitch),
                                                JSON_YAW: @(yaw),
                                                JSON_LAT: @(lat),
                                                JSON_LON: @(lon)
                                                }
    };
    
    NSError *error;
    NSData *messageData = [NSJSONSerialization dataWithJSONObject:messageDictionary options:0 error:&error];
    
    NSString *messageString = [[NSString alloc] initWithData:messageData encoding:NSUTF8StringEncoding];
    return messageString;
}

+ (NSString *)createTextMessage:(NSString *)text
{
    NSDictionary *messageDictionary = @{
                                        @"d": @{
                                                JSON_TEXT: text
                                                }
                                        };
    
    NSError *error;
    NSData *messageData = [NSJSONSerialization dataWithJSONObject:messageDictionary options:0 error:&error];
    
    NSString *messageString = [[NSString alloc] initWithData:messageData encoding:NSUTF8StringEncoding];
    return messageString;
}
+ (NSString *)createCommandMessage:(NSString *)command withValue:(NSString *)value
{
    NSDictionary *messageDictionary = @{
                                        @"d": @{
                                                command: value
                                                }
                                        };
    
    NSError *error;
    NSData *messageData = [NSJSONSerialization dataWithJSONObject:messageDictionary options:0 error:&error];
    
    NSString *messageString = [[NSString alloc] initWithData:messageData encoding:NSUTF8StringEncoding];
    return messageString;
}
+ (NSString *)createColourCommandMessage:(NSString *)colour
{
    NSString *red;
    NSString *green;
    NSString *blue;
    
    if ([colour  isEqual: @"Red"]) {
        red = @"255";
        green = @"0";
        blue = @"0";
    } else if ([colour isEqual:@"Green"]) {
        red = @"0";
        green = @"255";
        blue = @"0";
    } else if ([colour isEqual:@"Blue"]) {
        red = @"0";
        green = @"0";
        blue = @"255";
    } else {
        return NULL;
    }
    NSDictionary *messageDictionary = @{
                                        @"d": @{
                                                JSON_COLOR_R: red,
                                                JSON_COLOR_G: green,
                                                JSON_COLOR_B: blue,
                                                JSON_ALPHA: @"1.0"
                                                }
                                        };
    
    NSError *error;
    NSData *messageData = [NSJSONSerialization dataWithJSONObject:messageDictionary options:0 error:&error];
    
    NSString *messageString = [[NSString alloc] initWithData:messageData encoding:NSUTF8StringEncoding];
    return messageString;
}

@end
