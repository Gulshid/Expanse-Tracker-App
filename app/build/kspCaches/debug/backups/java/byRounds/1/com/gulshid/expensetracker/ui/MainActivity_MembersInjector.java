package com.gulshid.expensetracker.ui;

import com.google.firebase.auth.FirebaseAuth;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;

@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<FirebaseAuth> firebaseAuthProvider;

  public MainActivity_MembersInjector(Provider<FirebaseAuth> firebaseAuthProvider) {
    this.firebaseAuthProvider = firebaseAuthProvider;
  }

  public static MembersInjector<MainActivity> create(Provider<FirebaseAuth> firebaseAuthProvider) {
    return new MainActivity_MembersInjector(firebaseAuthProvider);
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectFirebaseAuth(instance, firebaseAuthProvider.get());
  }

  @InjectedFieldSignature("com.gulshid.expensetracker.ui.MainActivity.firebaseAuth")
  public static void injectFirebaseAuth(MainActivity instance, FirebaseAuth firebaseAuth) {
    instance.firebaseAuth = firebaseAuth;
  }
}
