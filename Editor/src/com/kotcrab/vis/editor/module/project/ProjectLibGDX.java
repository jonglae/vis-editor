/*
 * Copyright 2014-2015 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kotcrab.vis.editor.module.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer.Tag;

public class ProjectLibGDX extends Project {
	/** Gradle root */
	@Tag(0) private String root;
	/** typically gradleRoot/core/src/ */
	@Tag(1) private String source;
	/** typically gradleRoot/android/asset/ */
	@Tag(2) private String assetsOutput;

	public ProjectLibGDX (String root, String source, String assetsOutput) {
		this.root = root;
		this.source = source;
		this.assetsOutput = assetsOutput;
	}

	@Override
	public void updateRoot (FileHandle projectDataFile) {
		root = projectDataFile.parent().parent().path();
	}

	@Override
	public String verifyIfCorrect () {
		if (getVisDirectory().exists()) return "This folder is already a VisEditor project. Use File->Load Project.";
		return null;
	}

	@Override
	public FileHandle getVisDirectory () {
		return Gdx.files.absolute(root).child("vis");
	}

	@Override
	public FileHandle getAssetOutputDirectory () {
		return Gdx.files.absolute(root + assetsOutput);
	}

	public String getRoot () {
		return root;
	}
}
