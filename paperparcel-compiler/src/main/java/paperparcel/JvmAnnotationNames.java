package paperparcel;

import com.google.common.collect.ImmutableList;

public final class JvmAnnotationNames {
  static final ImmutableList<String> NONNULL_BY_DEFAULT_ANNOTATIONS = ImmutableList.of(
      "javax.annotation.ParametersAreNonnullByDefault",
      "com.android.annotations.NonNullByDefault"
  );

  static final ImmutableList<String> NULLABLE_BY_DEFAULT_ANNOTATIONS = ImmutableList.of(
      "javax.annotation.ParametersAreNullableByDefault"
  );

  static final String JAVAX_NONNULL_ANNOTATION = "javax.annotation.Nonnull";

  static final ImmutableList<String> NOT_NULL_ANNOTATIONS = ImmutableList.of(
      "org.jetbrains.annotations.NotNull",
      "edu.umd.cs.findbugs.annotations.NonNull",
      "android.support.annotation.NonNull",
      "com.android.annotations.NonNull",
      "org.eclipse.jdt.annotation.NonNull",
      "org.checkerframework.checker.nullness.qual.NonNull",
      "lombok.NonNull"
  );

  static final ImmutableList<String> NULLABLE_ANNOTATIONS = ImmutableList.of(
      "org.jetbrains.annotations.Nullable",
      "android.support.annotation.Nullable",
      "com.android.annotations.Nullable",
      "org.eclipse.jdt.annotation.Nullable",
      "org.checkerframework.checker.nullness.qual.Nullable",
      "javax.annotation.Nullable",
      "javax.annotation.CheckForNull",
      "edu.umd.cs.findbugs.annotations.CheckForNull",
      "edu.umd.cs.findbugs.annotations.Nullable",
      "edu.umd.cs.findbugs.annotations.PossiblyNull"
  );

  private JvmAnnotationNames() {
    throw new AssertionError("No instances.");
  }
}
