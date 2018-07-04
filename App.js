/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

 //https://github.com/zo0r/react-native-push-notification/issues/160
 //What is the method can be used to send an event from native module to JS？
 //https://github.com/facebook/react-native/issues/8714


import React, { Component } from 'react';
import {
  Platform, StyleSheet, Text, TextInput, View,NativeModules,NativeEventEmitter,
  Button, TouchableOpacity
} from 'react-native';

const firebase = NativeModules.FirebaseUtil;

export default class App extends Component {
  constructor() {
    super();
    this.state = {
      status: false,
      user: null,
      email: 'ericloew@hotmail.com',
      password: 'test#1',
    };
    this.onLogin = this.onLogin.bind(this);
  }

  componentDidMount() {

    const event = new NativeEventEmitter(firebase)   

    event.addListener('onAuthState', (data) => {
      console.log('onAuthState')
      if (data.user == null) {
        console.log('onAuthState no user')
        this.setState({
          status: false,
          user: null,
        });
      }
      else {
        console.log('onAuthState user')
        this.setState({
          status: true,
          user: data.user,
        });
      }
    })

    firebase.addAuthStateListener()
  }
   
  componentWillUnmount() {
    firebase.removeAuthStateListener();
  }

  handleEmail = (text) => {
    this.setState({ email: text })
  }

  handlePassword = (text) => {
      this.setState({ password: text })
  }

onLogin = (email, password) => {
  firebase.signInWithEmailAndPassword(email, password)
    .then((user) => {
      alert(user.email + ' logged in.')
    })
    .catch((error) => {
      alert(error.messsage)
    });
}

onRegister = (email, password) => {
  firebase.createUserWithEmailAndPassword(email, password)
    .then((user) => {
      alert(user.email + ' registered.')
    })
    .catch((error) => {
      alert(error.message)
    });
}

onSignOut = () => {
  firebase.signOut()
    .then(() => {
      this.setState({ status: false, });
    })
    .catch((error) => {
      alert(error.messsage)
    });
}

  render() {
    const status = this.state.status;
    return (
                        <View style={styles.container}>
        {status ? (
          <View>
            <Text>{(this.state.user != null)? this.state.user.email: ''}</Text>
            <TouchableOpacity
            style = {styles.submitButton}
            onPress = {
              () => this.onSignOut()
            }>
            <Text style = {styles.submitButtonText}> Sign Out </Text>
            </TouchableOpacity>
          </View>
        ) : (
          <View style={styles.container}>
            <View style={styles.inputContainer}>  
              <TextInput style={styles.inputStyle} autoCorrect={false} autoCapitalize = "none"
                placeholder="Email Address" 
                onChangeText = {this.handleEmail}>{this.state.email}</TextInput>      
            </View>
            <View style={styles.inputContainer}>
              <TextInput style={styles.inputStyle} autoCorrect={false} autoCapitalize = "none" secureTextEntry
                  placeholder="Password" 
                  onChangeText = {this.handlePassword}>{this.state.password}</TextInput> 
            </View>
            <TouchableOpacity style = {styles.registerButton}
               onPress = {
                  () => this.onRegister(this.state.email, this.state.password)
               }>
               <Text style = {styles.registerButtonText}> Register </Text>
            </TouchableOpacity>
            <TouchableOpacity style = {styles.submitButton}
               onPress = {
                  () => this.onLogin(this.state.email, this.state.password)
               }>
               <Text style = {styles.submitButtonText}> already registered. login me! </Text>
            </TouchableOpacity>
          </View>
        )}
      </View>             
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  container2: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: 'red',
  },

  container1: {
    flex: 1,
  
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
 
  inputContainer: {
    flexDirection: 'row',
    borderColor: '#000',
    alignItems: 'center',
    backgroundColor: 'yellow',
  },
  inputStyle: {
    flex: 1,
  },

  input: {
    margin: 15,
    height: 40,
    borderColor: '#7a42f4',
    borderWidth: 1
  },
  registerButton: {
      flexDirection: 'row',
      alignSelf: 'stretch',
      justifyContent: 'center',
      backgroundColor: '#7a42f4',
      padding: 10,
      margin: 15,
      height: 45,
      
  },
  registerButtonText:{
    fontSize:20,
    color: 'white'
  },
    
  submitButton: {
    flexDirection: 'row',
    //backgroundColor: 'white',
    padding: 10,
    margin: 15,
    height: 45,
    
  },
  submitButtonText:{
    color: 'blue',
    textAlign: 'center',
    fontSize: 20,
    textDecorationLine: 'underline',
  },
});
  
  
  