//
//  FirebaseUtil.m
//  FirebaseAuth
//
//  Created by Eric Loew on 6/29/18.
//  Copyright © 2018 Facebook. All rights reserved.
//

#import "FirebaseUtil.h"

@implementation FirebaseUtil
RCT_EXPORT_MODULE();

- (id)init {
  self = [super init];
  
  if (self != nil) {
    _authStateHandlers = [[NSMutableDictionary alloc] init];  //TODO: Make single instance instead of collection
  }
  return self;
}

#pragma mark - Auth State Listener
RCT_EXPORT_METHOD(addAuthStateListener) {

  if (!self.handle ) {
    self.handle = [[FIRAuth auth] addAuthStateDidChangeListener:^(FIRAuth *_Nonnull auth, FIRUser *_Nullable user) {
      if (user != nil) {
        [self sendEvent:self name:onAuthState body:@{@"user": [self userToDict:user]}];
      }
      else {
        [self sendEvent:self name:onAuthState body:@{}];
      }
    }];
  }
}

RCT_EXPORT_METHOD(removeAuthStateListener) {
  if (self.handle) {
    [[FIRAuth auth] removeAuthStateDidChangeListener:self.handle];
  }
}


//https://firebase.google.com/docs/auth/ios/password-auth
#pragma mark - Create User
RCT_EXPORT_METHOD(createUserWithEmailAndPassword:(NSString *)email password:(NSString*) password resolve:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
  
  self.resolve = resolve;
  self.reject = reject;
  
  [[FIRAuth auth] createUserWithEmail:email
                              password:password
                              completion:^(FIRAuthDataResult * _Nullable authResult, NSError * _Nullable error) {
                             
                                if (error) {
                                  [self promiseError: [error localizedDescription]];
                                }
                                else {
                                  FIRUser *user = [FIRAuth auth].currentUser;
                                  [self promiseUser:user];
                                }
                           }];
}

#pragma mark - Sign In
RCT_EXPORT_METHOD(signInWithEmailAndPassword:(NSString *)email password:(NSString*) password resolve:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
  
  self.resolve = resolve;
  self.reject = reject;
  
  [[FIRAuth auth] signInWithEmail:email
                          password:password
                          completion:^(FIRAuthDataResult * _Nullable authResult, NSError * _Nullable error) {
                         
                            if (error) {
                              [self promiseError: @"Error"];
                            }
                            else {
                              FIRUser *user = [FIRAuth auth].currentUser;
                              [self promiseUser:user];
                            }
                       }];
}

#pragma mark - Sign Out
RCT_EXPORT_METHOD(signOut:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
  
  self.resolve = resolve;
  self.reject = reject;
  
  FIRUser *user = [FIRAuth auth].currentUser;
  if (user) {
    NSError *error;
    [[FIRAuth auth] signOut:&error];
    if (!error)
      [self promiseValue:@"Signed Out"];
    else
      [self promiseError:@"Auth Exception"];
  } else {
    [self promiseError:@"No User signed in"];
  }
}

#pragma mark - Helpers

- (void)promiseError:(NSString *)message  {
  self.reject(@"", message, nil);
}

- (void)promiseValue:(NSString *)value  {
  
  self.resolve(@{
                 @"value": value
                 });
}

- (void)promiseUser:(FIRUser *)user  {
  
  NSDictionary* dict = [self userToDict: user];
  
  self.resolve(dict);
}

- (NSDictionary *)userToDict:(FIRUser *)user {
  return @{
           @"displayName": user.displayName ? user.displayName : [NSNull null],
           @"email": user.email ? user.email : [NSNull null],
           @"photoURL": user.photoURL ? [user.photoURL absoluteString] : [NSNull null]
           };
}

- (void)sendEvent:(RCTEventEmitter *)emitter name:(NSString *)name body:(id)body {
  @try {
    if (emitter.bridge) {
      [emitter sendEventWithName:name body:body];
    }
  }
  @catch (NSException *error) {
    NSLog(@"An error occurred in jsEvent: %@", [error debugDescription]);
  }
}

#pragma mark - RCTEventEmitter

- (NSArray<NSString *> *)supportedEvents {
  return @[onAuthState];
}

@end

