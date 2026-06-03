package com.gulshid.expensetracker.ui.expense;

import com.google.firebase.auth.FirebaseAuth;
import com.gulshid.expensetracker.domain.usecase.AddExpenseUseCase;
import com.gulshid.expensetracker.domain.usecase.DeleteExpenseUseCase;
import com.gulshid.expensetracker.domain.usecase.GetExpensesUseCase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class ExpenseViewModel_Factory implements Factory<ExpenseViewModel> {
  private final Provider<GetExpensesUseCase> getExpensesUseCaseProvider;

  private final Provider<AddExpenseUseCase> addExpenseUseCaseProvider;

  private final Provider<DeleteExpenseUseCase> deleteExpenseUseCaseProvider;

  private final Provider<FirebaseAuth> firebaseAuthProvider;

  public ExpenseViewModel_Factory(Provider<GetExpensesUseCase> getExpensesUseCaseProvider,
      Provider<AddExpenseUseCase> addExpenseUseCaseProvider,
      Provider<DeleteExpenseUseCase> deleteExpenseUseCaseProvider,
      Provider<FirebaseAuth> firebaseAuthProvider) {
    this.getExpensesUseCaseProvider = getExpensesUseCaseProvider;
    this.addExpenseUseCaseProvider = addExpenseUseCaseProvider;
    this.deleteExpenseUseCaseProvider = deleteExpenseUseCaseProvider;
    this.firebaseAuthProvider = firebaseAuthProvider;
  }

  @Override
  public ExpenseViewModel get() {
    return newInstance(getExpensesUseCaseProvider.get(), addExpenseUseCaseProvider.get(), deleteExpenseUseCaseProvider.get(), firebaseAuthProvider.get());
  }

  public static ExpenseViewModel_Factory create(
      Provider<GetExpensesUseCase> getExpensesUseCaseProvider,
      Provider<AddExpenseUseCase> addExpenseUseCaseProvider,
      Provider<DeleteExpenseUseCase> deleteExpenseUseCaseProvider,
      Provider<FirebaseAuth> firebaseAuthProvider) {
    return new ExpenseViewModel_Factory(getExpensesUseCaseProvider, addExpenseUseCaseProvider, deleteExpenseUseCaseProvider, firebaseAuthProvider);
  }

  public static ExpenseViewModel newInstance(GetExpensesUseCase getExpensesUseCase,
      AddExpenseUseCase addExpenseUseCase, DeleteExpenseUseCase deleteExpenseUseCase,
      FirebaseAuth firebaseAuth) {
    return new ExpenseViewModel(getExpensesUseCase, addExpenseUseCase, deleteExpenseUseCase, firebaseAuth);
  }
}
