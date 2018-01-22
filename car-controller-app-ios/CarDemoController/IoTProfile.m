/*
 Licensed Materials - Property of IBM
 
 © Copyright IBM Corporation 2014,2015. All Rights Reserved.
 
 This licensed material is sample code intended to aid the licensee in the development of software for the Apple iOS and OS X platforms . This sample code is  provided only for education purposes and any use of this sample code to develop software requires the licensee obtain and comply with the license terms for the appropriate Apple SDK (Developer or Enterprise edition).  Subject to the previous conditions, the licensee may use, copy, and modify the sample code in any form without payment to IBM for the purposes of developing software for the Apple iOS and OS X platforms.
 
 Notwithstanding anything to the contrary, IBM PROVIDES THE SAMPLE SOURCE CODE ON AN "AS IS" BASIS AND IBM DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, ANY IMPLIED WARRANTIES OR CONDITIONS OF MERCHANTABILITY, SATISFACTORY QUALITY, FITNESS FOR A PARTICULAR PURPOSE, TITLE, AND ANY WARRANTY OR CONDITION OF NON-INFRINGEMENT. IBM SHALL NOT BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY OR ECONOMIC CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OR OPERATION OF THE SAMPLE SOURCE CODE. IBM SHALL NOT BE LIABLE FOR LOSS OF, OR DAMAGE TO, DATA, OR FOR LOST PROFITS, BUSINESS REVENUE, GOODWILL, OR ANTICIPATED SAVINGS. IBM HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS OR MODIFICATIONS TO THE SAMPLE SOURCE CODE.
 */

// David Wray - Modifications for Solace Connected Car Demo © Copyright 2018

//
//  IoTProfile.m
//  IoTstarter
//

#import "AppDelegate.h"
#import "Constants.h"
#import "IoTProfile.h"

/**
 */

@implementation IoTProfile

- (id)initWithName:(NSString *)name dictionary:(NSMutableDictionary *)dictionary
{
    if (self = [super init])
    {
        self.profileName = name;
        self.routerAddress = [dictionary objectForKey:IOTRouterAddress];
        self.userName = [dictionary objectForKey:IOTUserName];
        self.password = [dictionary objectForKey:IOTPassword];
    }
    return self;
}

- (id)initWithName:(NSString *)name
      routerAddress:(NSString *)organization
          userName:(NSString *)deviceID
password:(NSString *)authorizationToken
{
    if (self = [super init])
    {
        self.profileName = name;
        self.routerAddress = organization;
        self.userName = deviceID;
        self.password = authorizationToken;
    }
    return self;
}

- (NSMutableDictionary *)createDictionaryFromProfile
{
    NSMutableDictionary *dictionary = [[NSMutableDictionary alloc] init];
    
    [dictionary setObject:self.routerAddress forKey:IOTRouterAddress];
    [dictionary setObject:self.userName forKey:IOTUserName];
    [dictionary setObject:self.password forKey:IOTPassword];
    
    return dictionary;
}

@end
