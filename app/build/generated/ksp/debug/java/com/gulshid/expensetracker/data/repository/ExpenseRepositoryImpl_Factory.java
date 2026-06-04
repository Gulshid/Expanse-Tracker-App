package com.gulshid.expensetracker.data.repository;

import com.google.firebase.firestore.FirebaseFirestore;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class ExpenseRepositoryImpl_Factory implements Factory<ExpenseRepositoryImpl> {
  private final Provider<FirebaseFirestore> firestoreProvider;

  public ExpenseRepositoryImpl_Factory(Provider<FirebaseFirestore> firestoreProvider) {
    this.firestoreProvider = firestoreProvider;
  }

  @Override
  public ExpenseRepositoryImpl get() {
    return newInstance(firestoreProvider.get());
  }

  public static ExpenseRepositoryImpl_Factory create(
      Provider<FirebaseFirestore> firestoreProvider) {
    return new ExpenseRepositoryImpl_Factory(firestoreProvider);
  }

  public static ExpenseRepositoryImpl newInstance(FirebaseFirestore firestore) {
    return new ExpenseRepositoryImpl(firestore);
  }
}
