package com.gulshid.expensetracker.domain.usecase;

import com.gulshid.expensetracker.domain.repository.ExpenseRepository;
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
public final class AddExpenseUseCase_Factory implements Factory<AddExpenseUseCase> {
  private final Provider<ExpenseRepository> expenseRepositoryProvider;

  public AddExpenseUseCase_Factory(Provider<ExpenseRepository> expenseRepositoryProvider) {
    this.expenseRepositoryProvider = expenseRepositoryProvider;
  }

  @Override
  public AddExpenseUseCase get() {
    return newInstance(expenseRepositoryProvider.get());
  }

  public static AddExpenseUseCase_Factory create(
      Provider<ExpenseRepository> expenseRepositoryProvider) {
    return new AddExpenseUseCase_Factory(expenseRepositoryProvider);
  }

  public static AddExpenseUseCase newInstance(ExpenseRepository expenseRepository) {
    return new AddExpenseUseCase(expenseRepository);
  }
}
