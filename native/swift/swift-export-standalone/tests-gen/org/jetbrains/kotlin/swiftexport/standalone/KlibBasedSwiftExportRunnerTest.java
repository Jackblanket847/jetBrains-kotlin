/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.swiftexport.standalone;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.kotlin.test.util.KtTestUtil;
import org.junit.jupiter.api.Tag;
import org.jetbrains.kotlin.konan.test.blackbox.support.group.FirPipeline;
import org.jetbrains.kotlin.konan.test.blackbox.support.group.UseStandardTestCaseGroupProvider;
import org.jetbrains.kotlin.test.TestMetadata;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.regex.Pattern;

/** This class is generated by {@link org.jetbrains.kotlin.generators.tests.native.swift.sir.GenerateSirTestsKt}. DO NOT MODIFY MANUALLY */
@SuppressWarnings("all")
@TestMetadata("native/swift/swift-export-standalone/testData/generation")
@TestDataPath("$PROJECT_ROOT")
@Tag("frontend-fir")
@FirPipeline()
@UseStandardTestCaseGroupProvider()
public class KlibBasedSwiftExportRunnerTest extends AbstractKlibBasedSwiftRunnerTest {
  @Test
  public void testAllFilesPresentInGeneration() {
    KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("native/swift/swift-export-standalone/testData/generation"), Pattern.compile("^([^\\.]+)$"), null, false);
  }

  @Test
  @TestMetadata("any")
  public void testAny() {
    runTest("native/swift/swift-export-standalone/testData/generation/any/");
  }

  @Test
  @TestMetadata("classes")
  public void testClasses() {
    runTest("native/swift/swift-export-standalone/testData/generation/classes/");
  }

  @Test
  @TestMetadata("consuming_dependencies")
  public void testConsuming_dependencies() {
    runTest("native/swift/swift-export-standalone/testData/generation/consuming_dependencies/");
  }

  @Test
  @TestMetadata("functionAndClassWithSameName")
  public void testFunctionAndClassWithSameName() {
    runTest("native/swift/swift-export-standalone/testData/generation/functionAndClassWithSameName/");
  }

  @Test
  @TestMetadata("functions")
  public void testFunctions() {
    runTest("native/swift/swift-export-standalone/testData/generation/functions/");
  }

  @Test
  @TestMetadata("inheritance")
  public void testInheritance() {
    runTest("native/swift/swift-export-standalone/testData/generation/inheritance/");
  }

  @Test
  @TestMetadata("inner_classes")
  public void testInner_classes() {
    runTest("native/swift/swift-export-standalone/testData/generation/inner_classes/");
  }

  @Test
  @TestMetadata("no_package")
  public void testNo_package() {
    runTest("native/swift/swift-export-standalone/testData/generation/no_package/");
  }

  @Test
  @TestMetadata("nothing_type")
  public void testNothing_type() {
    runTest("native/swift/swift-export-standalone/testData/generation/nothing_type/");
  }

  @Test
  @TestMetadata("nullable_type")
  public void testNullable_type() {
    runTest("native/swift/swift-export-standalone/testData/generation/nullable_type/");
  }

  @Test
  @TestMetadata("package_flattening")
  public void testPackage_flattening() {
    runTest("native/swift/swift-export-standalone/testData/generation/package_flattening/");
  }

  @Test
  @TestMetadata("package_flattening_invalid_target")
  public void testPackage_flattening_invalid_target() {
    runTest("native/swift/swift-export-standalone/testData/generation/package_flattening_invalid_target/");
  }

  @Test
  @TestMetadata("package_flattening_missing_target")
  public void testPackage_flattening_missing_target() {
    runTest("native/swift/swift-export-standalone/testData/generation/package_flattening_missing_target/");
  }

  @Test
  @TestMetadata("single_module_production")
  public void testSingle_module_production() {
    runTest("native/swift/swift-export-standalone/testData/generation/single_module_production/");
  }

  @Test
  @TestMetadata("strings")
  public void testStrings() {
    runTest("native/swift/swift-export-standalone/testData/generation/strings/");
  }

  @Test
  @TestMetadata("type_reference")
  public void testType_reference() {
    runTest("native/swift/swift-export-standalone/testData/generation/type_reference/");
  }

  @Test
  @TestMetadata("typealiases")
  public void testTypealiases() {
    runTest("native/swift/swift-export-standalone/testData/generation/typealiases/");
  }

  @Test
  @TestMetadata("unsupportedDeclarationsReporting")
  public void testUnsupportedDeclarationsReporting() {
    runTest("native/swift/swift-export-standalone/testData/generation/unsupportedDeclarationsReporting/");
  }

  @Test
  @TestMetadata("variables")
  public void testVariables() {
    runTest("native/swift/swift-export-standalone/testData/generation/variables/");
  }
}
