package com.gulshid.expensetracker.ui.dashboard;

import com.google.firebase.auth.FirebaseAuth;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
    "cast"
})
public final class DashboardFragment_MembersInjector implements MembersInjector<DashboardFragment> {
  private final Provider<FirebaseAuth> firebaseAuthProvider;

  public DashboardFragment_MembersInjector(Provider<FirebaseAuth> firebaseAuthProvider) {
    this.firebaseAuthProvider = firebaseAuthProvider;
  }

  public static MembersInjector<DashboardFragment> create(
      Provider<FirebaseAuth> firebaseAuthProvider) {
    return new DashboardFragment_MembersInjector(firebaseAuthProvider);
  }

  @Override
  public void injectMembers(DashboardFragment instance) {
    injectFirebaseAuth(instance, firebaseAuthProvider.get());
  }

  @InjectedFieldSignature("com.gulshid.expensetracker.ui.dashboard.DashboardFragment.firebaseAuth")
  public static void injectFirebaseAuth(DashboardFragment instance, FirebaseAuth firebaseAuth) {
    instance.firebaseAuth = firebaseAuth;
  }
}
