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
public final class DeleteExpenseUseCase_Factory implements Factory<DeleteExpenseUseCase> {
  private final Provider<ExpenseRepository> expenseRepositoryProvider;

  public DeleteExpenseUseCase_Factory(Provider<ExpenseRepository> expenseRepositoryProvider) {
    this.expenseRepositoryProvider = expenseRepositoryProvider;
  }

  @Override
  public DeleteExpenseUseCase get() {
    return newInstance(expenseRepositoryProvider.get());
  }

  public static DeleteExpenseUseCase_Factory create(
      Provider<ExpenseRepository> expenseRepositoryProvider) {
    return new DeleteExpenseUseCase_Factory(expenseRepositoryProvider);
  }

  public static DeleteExpenseUseCase newInstance(ExpenseRepository expenseRepository) {
    return new DeleteExpenseUseCase(expenseRepository);
  }
}
