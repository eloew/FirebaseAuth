//
//  FirebaseUtil.h
//  FirebaseAuth
//
//  Created by Eric Loew on 6/29/18.
//  Copyright © 2018 Facebook. All rights reserved.
//

//events
//https://github.com/facebook/react-native/issues/8714


#ifndef FirebaseUtil_h
#define FirebaseUtil_h

#import <Foundation/Foundation.h>

static NSString *const onAuthState = @"onAuthState";
static NSString *const appDisplayName = @"bldm";



#import "Firebase.h"
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>

@interface FirebaseUtil : RCTEventEmitter<RCTBridgeModule>

@property (nonatomic, strong) RCTPromiseRejectBlock reject;
@property (nonatomic, strong) RCTPromiseResolveBlock resolve;
//@property (nonatomic, strong) NSString * appDisplayName;
@property NSMutableDictionary *authStateHandlers;
@property(strong, nonatomic) FIRAuthStateDidChangeListenerHandle handle;

@end
#endif
