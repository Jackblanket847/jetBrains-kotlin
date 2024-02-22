/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.analysis.api.fir.test.cases.generated.cases.components.psiTypeProvider;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.kotlin.test.util.KtTestUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.analysis.api.fir.test.configurators.AnalysisApiFirTestConfiguratorFactory;
import org.jetbrains.kotlin.analysis.test.framework.test.configurators.AnalysisApiTestConfiguratorFactoryData;
import org.jetbrains.kotlin.analysis.test.framework.test.configurators.AnalysisApiTestConfigurator;
import org.jetbrains.kotlin.analysis.test.framework.test.configurators.TestModuleKind;
import org.jetbrains.kotlin.analysis.test.framework.test.configurators.FrontendKind;
import org.jetbrains.kotlin.analysis.test.framework.test.configurators.AnalysisSessionMode;
import org.jetbrains.kotlin.analysis.test.framework.test.configurators.AnalysisApiMode;
import org.jetbrains.kotlin.analysis.api.impl.base.test.cases.components.psiTypeProvider.AbstractAnalysisApiPsiTypeProviderTest;
import org.jetbrains.kotlin.test.TestMetadata;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.regex.Pattern;

/** This class is generated by {@link org.jetbrains.kotlin.generators.tests.analysis.api.GenerateAnalysisApiTestsKt}. DO NOT MODIFY MANUALLY */
@SuppressWarnings("all")
@TestMetadata("analysis/analysis-api/testData/components/psiTypeProvider/psiType/forDeclaration")
@TestDataPath("$PROJECT_ROOT")
public class FirIdeNormalAnalysisSourceModuleAnalysisApiPsiTypeProviderTestGenerated extends AbstractAnalysisApiPsiTypeProviderTest {
  @NotNull
  @Override
  public AnalysisApiTestConfigurator getConfigurator() {
    return AnalysisApiFirTestConfiguratorFactory.INSTANCE.createConfigurator(
      new AnalysisApiTestConfiguratorFactoryData(
        FrontendKind.Fir,
        TestModuleKind.Source,
        AnalysisSessionMode.Normal,
        AnalysisApiMode.Ide
      )
    );
  }

  @Test
  @TestMetadata("actual_jvmInline_typealias.kt")
  public void testActual_jvmInline_typealias() {
    runTest("analysis/analysis-api/testData/components/psiTypeProvider/psiType/forDeclaration/actual_jvmInline_typealias.kt");
  }

  @Test
  @TestMetadata("actual_typealias.kt")
  public void testActual_typealias() {
    runTest("analysis/analysis-api/testData/components/psiTypeProvider/psiType/forDeclaration/actual_typealias.kt");
  }

  @Test
  public void testAllFilesPresentInForDeclaration() {
    KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("analysis/analysis-api/testData/components/psiTypeProvider/psiType/forDeclaration"), Pattern.compile("^(.+)\\.kt$"), null, true);
  }

  @Test
  @TestMetadata("anonymousObject_exposedAsReturnValue.kt")
  public void testAnonymousObject_exposedAsReturnValue() {
    runTest("analysis/analysis-api/testData/components/psiTypeProvider/psiType/forDeclaration/anonymousObject_exposedAsReturnValue.kt");
  }

  @Test
  @TestMetadata("duplicatedClass_functionParameter.kt")
  public void testDuplicatedClass_functionParameter() {
    runTest("analysis/analysis-api/testData/components/psiTypeProvider/psiType/forDeclaration/duplicatedClass_functionParameter.kt");
  }

  @Test
  @TestMetadata("errorTypeInNestedTypeArgument.kt")
  public void testErrorTypeInNestedTypeArgument() {
    runTest("analysis/analysis-api/testData/components/psiTypeProvider/psiType/forDeclaration/errorTypeInNestedTypeArgument.kt");
  }

  @Test
  @TestMetadata("localClass_exposedAsMemberInAnonymousObject.kt")
  public void testLocalClass_exposedAsMemberInAnonymousObject() {
    runTest("analysis/analysis-api/testData/components/psiTypeProvider/psiType/forDeclaration/localClass_exposedAsMemberInAnonymousObject.kt");
  }

  @Test
  @TestMetadata("localClass_exposedAsReturnValue.kt")
  public void testLocalClass_exposedAsReturnValue() {
    runTest("analysis/analysis-api/testData/components/psiTypeProvider/psiType/forDeclaration/localClass_exposedAsReturnValue.kt");
  }

  @Test
  @TestMetadata("localClass_exposedAsReturnValue_privateFunction.kt")
  public void testLocalClass_exposedAsReturnValue_privateFunction() {
    runTest("analysis/analysis-api/testData/components/psiTypeProvider/psiType/forDeclaration/localClass_exposedAsReturnValue_privateFunction.kt");
  }

  @Test
  @TestMetadata("localClass_localFunctionInSameScope.kt")
  public void testLocalClass_localFunctionInSameScope() {
    runTest("analysis/analysis-api/testData/components/psiTypeProvider/psiType/forDeclaration/localClass_localFunctionInSameScope.kt");
  }

  @Test
  @TestMetadata("localClass_localPropertyInSameScope.kt")
  public void testLocalClass_localPropertyInSameScope() {
    runTest("analysis/analysis-api/testData/components/psiTypeProvider/psiType/forDeclaration/localClass_localPropertyInSameScope.kt");
  }

  @Test
  @TestMetadata("localClass_localPropertyInSampeScope_functionalType.kt")
  public void testLocalClass_localPropertyInSampeScope_functionalType() {
    runTest("analysis/analysis-api/testData/components/psiTypeProvider/psiType/forDeclaration/localClass_localPropertyInSampeScope_functionalType.kt");
  }

  @Test
  @TestMetadata("localClass_memberFunction.kt")
  public void testLocalClass_memberFunction() {
    runTest("analysis/analysis-api/testData/components/psiTypeProvider/psiType/forDeclaration/localClass_memberFunction.kt");
  }

  @Test
  @TestMetadata("suspendFunctionValueParameterNoStdlib.kt")
  public void testSuspendFunctionValueParameterNoStdlib() {
    runTest("analysis/analysis-api/testData/components/psiTypeProvider/psiType/forDeclaration/suspendFunctionValueParameterNoStdlib.kt");
  }

  @Test
  @TestMetadata("suspendFunctionValueParameterWithStdlib.kt")
  public void testSuspendFunctionValueParameterWithStdlib() {
    runTest("analysis/analysis-api/testData/components/psiTypeProvider/psiType/forDeclaration/suspendFunctionValueParameterWithStdlib.kt");
  }

  @Test
  @TestMetadata("wildcardSuppression_false.kt")
  public void testWildcardSuppression_false() {
    runTest("analysis/analysis-api/testData/components/psiTypeProvider/psiType/forDeclaration/wildcardSuppression_false.kt");
  }

  @Test
  @TestMetadata("wildcardSuppression_true.kt")
  public void testWildcardSuppression_true() {
    runTest("analysis/analysis-api/testData/components/psiTypeProvider/psiType/forDeclaration/wildcardSuppression_true.kt");
  }
}
