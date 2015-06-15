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

package com.kotcrab.vis.editor.ui.dialog;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.editor.module.editor.FileChooserModule;
import com.kotcrab.vis.editor.module.editor.ProjectIOModule;
import com.kotcrab.vis.editor.module.project.ProjectGeneric;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.util.dialog.DialogUtils;
import com.kotcrab.vis.ui.util.form.FormValidator;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisValidableTextField;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;

public class NewProjectDialogGeneric extends VisTable {
	private VisValidableTextField projectRoot;
	private VisValidableTextField outputDirectory;

	private VisTextButton chooseRootButton;
	private VisTextButton chooseOutputButton;

	private VisLabel errorLabel;

	private VisTextButton cancelButton;
	private VisTextButton createButton;

	private NewProjectDialog dialog;
	private FileChooserModule fileChooserModule;
	private ProjectIOModule projectIO;

	public NewProjectDialogGeneric (NewProjectDialog dialog, FileChooserModule fileChooserModule, ProjectIOModule projectIO) {
		this.dialog = dialog;
		this.fileChooserModule = fileChooserModule;
		this.projectIO = projectIO;

		createUI();
		createListeners();
		createValidators();
	}

	private void createUI () {
		projectRoot = new VisValidableTextField("");
		outputDirectory = new VisValidableTextField("");
		chooseRootButton = new VisTextButton("Choose...");
		chooseOutputButton = new VisTextButton("Choose...");

		errorLabel = new VisLabel();
		errorLabel.setColor(Color.RED);

		TableUtils.setSpacingDefaults(this);
		columnDefaults(0).left();
		columnDefaults(1).expandX().fillX();

		row().padTop(4);
		add(new VisLabel("Project root"));
		add(projectRoot);
		add(chooseRootButton);
		row();

		add(new VisLabel("Output folder"));
		add(outputDirectory);
		add(chooseOutputButton);
		row();

		VisTable buttonTable = new VisTable(true);
		buttonTable.defaults().minWidth(70);

		cancelButton = new VisTextButton("Cancel");
		createButton = new VisTextButton("Create");
		createButton.setDisabled(true);

		buttonTable.add(errorLabel).fill().expand();
		buttonTable.add(cancelButton);
		buttonTable.add(createButton);

		add(buttonTable).colspan(3).fillX().expandX();
	}

	private void createListeners () {
		chooseRootButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				fileChooserModule.pickFileOrDirectory(new FileChooserAdapter() {
					@Override
					public void selected (FileHandle file) {
						projectRoot.setText(file.file().getAbsolutePath());
					}
				});
			}
		});

		chooseOutputButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				fileChooserModule.pickFileOrDirectory(new FileChooserAdapter() {
					@Override
					public void selected (FileHandle file) {
						outputDirectory.setText(file.file().getAbsolutePath());
					}
				});
			}
		});

		cancelButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				dialog.fadeOut();
			}
		});

		createButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				createProject();
			}
		});
	}

	private void createValidators () {
		FormValidator validator = new FormValidator(createButton, errorLabel);

		validator.notEmpty(projectRoot, "Project root path cannot be empty!");
		validator.notEmpty(outputDirectory, "Output folder field cannot be empty!");

		validator.directory(projectRoot, "Project folder is not a directory!");
		validator.directory(outputDirectory, "Output folder is not a directory!");
		validator.directoryEmpty(outputDirectory, "Output directory must be empty!");
	}

	private void createProject () {
		ProjectGeneric project = new ProjectGeneric(projectRoot.getText(), outputDirectory.getText());

		String error = project.verifyIfCorrect();
		if (error == null) {
			projectIO.createGenericProject(project);
			dialog.fadeOut();
		} else
			DialogUtils.showErrorDialog(getStage(), error);

	}
}
