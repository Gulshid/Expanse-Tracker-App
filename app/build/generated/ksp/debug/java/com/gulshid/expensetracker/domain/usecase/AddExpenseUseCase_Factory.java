package com.gulshid.expensetracker.domain.usecase;

import com.gulshid.expensetracker.domain.repository.ExpenseRepository;
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
