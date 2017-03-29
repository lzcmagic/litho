/**
 * Copyright (c) 2014-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.facebook.litho.processor;

import javax.lang.model.element.Modifier;

import java.util.ArrayList;
import java.util.List;

import com.facebook.litho.annotations.Param;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import static com.facebook.litho.specmodels.generator.GeneratorConstants.SPEC_INSTANCE_NAME;

/**
 * Provides a Builder for generating an implementation of
 * {@link com.facebook.litho.ComponentLifecycle.StateUpdate}
 */
public class StateUpdateImplClassBuilder {

  private static final String STATE_CONTAINER_PARAM_NAME = "stateContainer";
  private static final String STATE_CONTAINER_IMPL_NAME = "stateContainerImpl";
  private static final String STATE_UPDATE_OLD_COMPONENT_NAME = "oldComponent";
  private static final String STATE_UPDATE_NEW_COMPONENT_NAME = "newComponent";
  private static final String STATE_UPDATE_IMPL_NAME_SUFFIX = "StateUpdate";
  private static final String STATE_UPDATE_METHOD_NAME = "updateState";
  private static final String STATE_UPDATE_IS_LAZY_METHOD_NAME = "isLazyStateUpdate";

  private String mTarget;
  private String mStateUpdateClassName;
  private TypeName mImplClassName;
  private final List<Parameter> mParamsForStateUpdate = new ArrayList<>();
  private final List<Parameter> mStateValueParams = new ArrayList<>();
  private final List<Parameter> mSpecOnUpdateStateMethodParams = new ArrayList<>();
  private String mSpecOnUpdateStateMethodName;
  private ClassName mComponentStateUpdateInterface;
  private ClassName mComponentClassName;
  private Stages.StaticFlag mStaticFlag;
  private final List<String> mTypeParameters = new ArrayList<>();
  private ClassName mStateContainerClassName;
  private ClassName mStateContainerImplClassName;

  StateUpdateImplClassBuilder withTarget(String target) {
    mTarget = target;
    return this;
  }

  /**
   * Set name of class that will be generated by this builder.
   * @param className name
   */
  public StateUpdateImplClassBuilder withStateUpdateImplClassName(String className) {
    mStateUpdateClassName = className;
    return this;
  }

  public StateUpdateImplClassBuilder withStateContainerClassName(ClassName className) {
    mStateContainerClassName = className;
    return this;
  }

  public StateUpdateImplClassBuilder withStateContainerImplClassName(ClassName className) {
    mStateContainerImplClassName = className;
    return this;
  }

  /**
   * Set name of the inner class that holds the state.
   * @param className name
   */
  public StateUpdateImplClassBuilder withComponentImplClassName(TypeName className) {
    this.mImplClassName = className;
    return this;
  }

  /**
   * Set the name of the method annotated with
   *  {@link com.facebook.litho.annotations.OnUpdateState}
   * @param name method name
   */
  public StateUpdateImplClassBuilder withSpecOnUpdateStateMethodName(String name) {
    this.mSpecOnUpdateStateMethodName = name;
    return this;
  }

  /**
   * Set the parameters that are used to calculate the new value of a state.
   * @param params params used to update the state, annotated with {@link Param} in the
   *  {@link com.facebook.litho.annotations.OnUpdateState} method declaration
   */
  public StateUpdateImplClassBuilder withParamsForStateUpdate(List<Parameter> params) {
    this.mParamsForStateUpdate.addAll(params);
    return this;
  }

  public StateUpdateImplClassBuilder withParamForStateUpdate(Parameter param) {
    this.mParamsForStateUpdate.add(param);
    return this;
  }

  /**
   * Set the parameters that represent a state value that will be updated.
   * @param stateValueParams  state value parameters
   * @return
   */
  public StateUpdateImplClassBuilder withStateValueParams(List<Parameter> stateValueParams) {
    this.mStateValueParams.addAll(stateValueParams);
    return this;
  }

  public StateUpdateImplClassBuilder withStateValueParam(Parameter stateValueParam) {
    this.mStateValueParams.add(stateValueParam);
    return this;
  }

  public StateUpdateImplClassBuilder withStaticFlag(Stages.StaticFlag staticFlag) {
    mStaticFlag = staticFlag;
    return this;
  }

  /**
   * Set the params of the method annotated with
   *  {@link com.facebook.litho.annotations.OnUpdateState}
   * @param params params
   */
  public StateUpdateImplClassBuilder withSpecOnUpdateStateMethodParams(List<Parameter> params) {
    this.mSpecOnUpdateStateMethodParams.addAll(params);
    return this;
  }

  public StateUpdateImplClassBuilder withSpecOnUpdateStateMethodParam(Parameter param) {
    this.mSpecOnUpdateStateMethodParams.add(param);
    return this;
  }

  public StateUpdateImplClassBuilder withComponentStateUpdateInterface(
      ClassName componentStateUpdateInterface) {
    mComponentStateUpdateInterface = componentStateUpdateInterface;
    return this;
  }

  public StateUpdateImplClassBuilder withComponentClassName(
      ClassName componentClassName) {
    mComponentClassName = componentClassName;
    return this;
  }

  StateUpdateImplClassBuilder typeParameter(String typeParam) {
    mTypeParameters.add(typeParam);
    return this;
  }

  TypeSpec build() {
    final TypeSpec.Builder stateUpdateClassBuilder =
        TypeSpec.classBuilder(mStateUpdateClassName)
            .addModifiers(Modifier.PRIVATE)
            .addSuperinterface(mComponentStateUpdateInterface);

    if (mStaticFlag == Stages.StaticFlag.STATIC) {
      stateUpdateClassBuilder.addModifiers(Modifier.STATIC);
      for (String typeParam : mTypeParameters) {
        stateUpdateClassBuilder.addTypeVariable(TypeVariableName.get(typeParam));
      }
