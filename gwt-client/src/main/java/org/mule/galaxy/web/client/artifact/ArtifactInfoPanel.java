package org.mule.galaxy.web.client.artifact;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.Collection;
import java.util.Iterator;

import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.client.ArtifactForm;
import org.mule.galaxy.web.client.RegistryPanel;
import org.mule.galaxy.web.client.WorkspacePanel;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.client.util.Toolbox;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.ArtifactGroup;
import org.mule.galaxy.web.rpc.DependencyInfo;
import org.mule.galaxy.web.rpc.ExtendedArtifactInfo;
import org.mule.galaxy.web.rpc.WComment;

public class ArtifactInfoPanel extends AbstractComposite {


    private HorizontalPanel topPanel;
    private RegistryPanel registryPanel;
    private VerticalPanel rightGroup;
    private VerticalPanel panel;
    private FlowPanel commentsPanel;
    private ExtendedArtifactInfo info;

    public ArtifactInfoPanel(final RegistryPanel registryPanel, 
                             ArtifactGroup group,
                             ExtendedArtifactInfo info) {
        this.registryPanel = registryPanel;
        this.info = info;
        
        panel = new VerticalPanel();
        
        topPanel = new HorizontalPanel();
        topPanel.setStyleName("artifactTopPanel");
        
        panel.add(topPanel);

        FlexTable table = createColumnTable();
        
        final NameEditPanel nep = new NameEditPanel(registryPanel, info.getId(), 
                                                    (String) info.getValue(0),
                                                    info.getWorkspaceId());
        
        InlineFlowPanel nameLabelPanel = new InlineFlowPanel();
        nameLabelPanel.add(new Label("Name ["));
        Hyperlink editHL = new Hyperlink("Edit", "edit-property");
        editHL.addClickListener(new ClickListener() {

            public void onClick(Widget arg0) {
                nep.showEditPanel();
            }
            
        });
        nameLabelPanel.add(editHL);
        nameLabelPanel.add(new Label("]"));
        table.setWidget(0, 0, nameLabelPanel);
        table.setWidget(0, 1, nep);
        
        for (int i = 1; i < group.getColumns().size(); i++) {
            table.setText(i, 0, (String) group.getColumns().get(i));
        }
        
        int c = 1;
        for (; c < group.getColumns().size(); c++) {
            table.setText(c, 1, info.getValue(c));
        }
        
        initDescription(table, c);
        styleHeaderColumn(table);
        topPanel.add(table);
        
        rightGroup = new VerticalPanel();
        rightGroup.setStyleName("artifactInfoRightGroup");
        rightGroup.setSpacing(6);
        
        addArtifactLinks(registryPanel);
        
        topPanel.add(rightGroup);
        
        registryPanel.getRegistryService().getDependencyInfo(info.getId(), new AbstractCallback(registryPanel) {

            public void onSuccess(Object o) {
                initDependencies((Collection) o);
            }
            
        });
        
        panel.add(new ArtifactMetadataPanel(registryPanel, info));
        
        initComments();
        
        initWidget(panel);
    }

    private void initDescription(FlexTable table, int c) {
        final SimplePanel descPanel = new SimplePanel();
        
        InlineFlowPanel descLabelPanel = new InlineFlowPanel();
        descLabelPanel.setStyleName("artifactDescriptionPanel");
        descLabelPanel.add(new Label("Description ["));
        Hyperlink hl = new Hyperlink("Edit", "");
        hl.addClickListener(new ClickListener() {

            public void onClick(Widget w) {
                initDescriptionForm(descPanel);
            }
            
        });
        descLabelPanel.add(hl);
        descLabelPanel.add(new Label("]"));
        
        table.setWidget(c, 0, descLabelPanel);
        descPanel.add(new Label(info.getDescription()));
        table.setWidget(c, 1, descPanel);
    }

    private void initComments() {
        commentsPanel = new FlowPanel();
        commentsPanel.setStyleName("comments");
        
        Hyperlink addComment = new Hyperlink("Add", "add-comment");
        addComment.addClickListener(new AddCommentClickListener(commentsPanel, null));
        
        InlineFlowPanel commentTitlePanel = createTitleWithLink("Comments", addComment);
        Image img = new Image("images/feed-icon-14x14.png");
        img.addClickListener(new ClickListener() {

            public void onClick(Widget sender) {
                Window.open(info.getCommentsFeedLink(), null, null);
            }
            
        });
        img.setStyleName("feed-icon");
        commentTitlePanel.add(img);
        
        panel.add(commentTitlePanel);
        panel.add(commentsPanel);
        
        for (Iterator itr = info.getComments().iterator(); itr.hasNext();) {
            commentsPanel.add(createCommentPanel((WComment) itr.next()));
        }
    }

    private Widget createCommentPanel(WComment c) {
        final FlowPanel commentPanel = new FlowPanel();
        commentPanel.setStyleName("comment");
        
        InlineFlowPanel title = new InlineFlowPanel();
        title.setStyleName("commentTitle");
        Label userDateLabel = new Label(c.getUser() + " on " + c.getDate());
        
        Hyperlink replyLink = new Hyperlink("Reply", "reply-" + c.getId());
        replyLink.addClickListener(new AddCommentClickListener(commentPanel, c.getId()));
        title.add(replyLink);
        title.add(userDateLabel);
        
        commentPanel.add(title);
        
        Label commentBody = new Label(c.getText(), true);
        commentBody.setStyleName("commentText");
        
        commentPanel.add(commentBody);
        
        for (Iterator comments = c.getComments().iterator(); comments.hasNext();) {
            WComment child = (WComment) comments.next();
            
            SimplePanel nestedComment = new SimplePanel();
            nestedComment.setStyleName("nestedComment");
            
            Widget childPanel = createCommentPanel(child);
            nestedComment.add(childPanel);
            
            commentPanel.add(nestedComment);
        }
        return commentPanel;
    }

    protected void showAddComment(final Panel commentPanel, 
                                  final String parentId,
                                  final AddCommentClickListener replyClickListener) {
        if (replyClickListener.isShowingComment()) {
            return;

        }
        replyClickListener.setShowingComment(true);
        final VerticalPanel addCommentPanel = new VerticalPanel();
        addCommentPanel.setStyleName("addComment");
        
        final TextArea text = new TextArea();
        text.setCharacterWidth(60);
        text.setVisibleLines(5);
        addCommentPanel.add(text);
        
        HorizontalPanel buttons = new HorizontalPanel();
        buttons.setSpacing(10);
        buttons.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        
        final Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(new ClickListener() {
            public void onClick(Widget w) {
                commentPanel.remove(addCommentPanel);
                replyClickListener.setShowingComment(false);
            }
        });
        buttons.add(cancelButton);
        
        final Button addButton = new Button("Add");
        addButton.addClickListener(new ClickListener() {
            public void onClick(Widget w) {
                addComment(commentPanel, 
                           addCommentPanel, 
                           text,
                           cancelButton,
                           addButton,
                           parentId,
                           replyClickListener);
            }
        });
        buttons.add(addButton);
        addCommentPanel.add(buttons);
        addCommentPanel.setCellHorizontalAlignment(buttons, HasHorizontalAlignment.ALIGN_RIGHT);

        addCommentPanel.setVisible(true);
        
        if (!commentPanel.equals(commentsPanel)) {
            SimplePanel nested = new SimplePanel();
            nested.setStyleName("nestedComment");
            nested.add(addCommentPanel);
            commentPanel.add(addCommentPanel);
        } else {
            commentPanel.add(addCommentPanel);
        }
    }

    protected void addComment(final Panel parent,
                              final Panel addCommentPanel, 
                              final TextArea text, 
                              final Button cancelButton, 
                              final Button addButton, 
                              final String parentId,
                              final AddCommentClickListener replyClickListener) {

        cancelButton.setEnabled(false);
        addButton.setEnabled(false);
        text.setEnabled(false);
        
        registryPanel.getRegistryService().addComment(info.getId(), parentId, text.getText(), new AbstractCallback(registryPanel) {

            public void onFailure(Throwable caught) {
                super.onFailure(caught);
                
                cancelButton.setEnabled(true);
                addButton.setEnabled(true);
                text.setEnabled(true);
            }

            public void onSuccess(Object o) {
                parent.remove(addCommentPanel);
                
                Widget commentPanel = createCommentPanel((WComment) o);
                if (replyClickListener.commentPanel != commentsPanel) {
                    SimplePanel nestedComment = new SimplePanel();
                    nestedComment.setStyleName("nestedComment");
                    nestedComment.add(commentPanel);
                    commentPanel = nestedComment;
                }
                
                parent.add(commentPanel);
                replyClickListener.setShowingComment(false);
            }
            
        });
    }

    private void addArtifactLinks(final RegistryPanel registryPanel) {
        Hyperlink hl = new Hyperlink("View", "view-artifact");
        hl.addClickListener(new ClickListener() {

            public void onClick(Widget arg0) {
                Window.open(".." + info.getArtifactLink(), null, null);
            }
            
        });
        
        rightGroup.add(hl);
        hl = new Hyperlink("New Version", "new-artifact-version");
        hl.addClickListener(new ClickListener() {

            public void onClick(Widget arg0) {
                registryPanel.setMain(new ArtifactForm(registryPanel, info.getId()));
            }
            
        });
        rightGroup.add(hl);
        
        hl = new Hyperlink("Delete", "delete-artifact");
        hl.addClickListener(new ClickListener() {

            public void onClick(Widget arg0) {
                warnDelete();
            }
            
        });
        rightGroup.add(hl);
    }
    
    protected void warnDelete() {
        if (Window.confirm("Are you sure you want to delete this artifact")) {
            registryPanel.getRegistryService().delete(info.getId(), new AbstractCallback(registryPanel) {

                public void onSuccess(Object arg0) {
                    registryPanel.setMain(new WorkspacePanel(registryPanel));
                    registryPanel.setMessage("Artifact was deleted.");
                }
                
            });
        }
    }

    protected void initDependencies(Collection o) {
        Toolbox depPanel = new Toolbox(true);
        depPanel.setTitle("Dependencies");
        
        Toolbox depOnPanel = new Toolbox(true);
        depOnPanel.setTitle("Depended On By");
        
        boolean addedDeps = false;
        boolean addedDependedOn = false;
        
        for (Iterator itr = o.iterator(); itr.hasNext();) {
            final DependencyInfo info = (DependencyInfo) itr.next();
            
            Hyperlink hl = new Hyperlink(info.getArtifactName(), 
                                         "artifact-" + info.getArtifactId());
            hl.addClickListener(new ClickListener() {

                public void onClick(Widget arg0) {
                    registryPanel.setMain(new ArtifactPanel(registryPanel, 
                                                            info.getArtifactId()));
                }
            });
            
            if (info.isDependsOn()) {
                depPanel.add(hl);
                
                if (!addedDeps) {
                    rightGroup.add(depPanel);
                    addedDeps = true;
                }
            } else {
                depOnPanel.add(hl);
                
                if (!addedDependedOn) {
                    rightGroup.add(depOnPanel);
                    addedDependedOn = true;
                }
            }
        }
        topPanel.add(rightGroup);
    }

    private void initDescriptionForm(final SimplePanel descPanel) {
        descPanel.clear();
        
        VerticalPanel form = new VerticalPanel();
        final TextArea text = new TextArea();
        text.setCharacterWidth(60);
        text.setVisibleLines(8);
        text.setText(info.getDescription());
        form.add(text);

        HorizontalPanel buttons = new HorizontalPanel();
        form.add(buttons);
        buttons.setWidth("100%");
        buttons.setSpacing(10);
        buttons.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        
        final Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(new ClickListener() {
            public void onClick(Widget w) {
                descPanel.clear();
                descPanel.add(new Label(info.getDescription()));
            }
        });
        buttons.add(cancelButton);
        
        final Button addButton = new Button("Save");
        addButton.addClickListener(new ClickListener() {
            public void onClick(Widget w) {
                saveDescription(descPanel, text, cancelButton, addButton);
            }

           
        });
        buttons.add(addButton);
        
        descPanel.add(form);
    }

    protected void saveDescription(final SimplePanel descPanel, final TextArea text,
                                   final Button cancelButton, final Button addButton) {
        cancelButton.setEnabled(false);
        addButton.setEnabled(false);
       
        AbstractCallback callback = new AbstractCallback(registryPanel) {

            public void onFailure(Throwable caught) {
                super.onFailure(caught);
                cancelButton.setEnabled(true);
                addButton.setEnabled(true);
            }

            public void onSuccess(Object arg0) {
                descPanel.clear();
                descPanel.add(new Label(text.getText()));
                info.setDescription(text.getText());
            }

        };
        registryPanel.getRegistryService().setDescription(info.getId(), text.getText(), callback);
          
    }
    private final class AddCommentClickListener implements ClickListener {
        private final Panel commentPanel;
        private boolean showingComment;
        private String parentId;
        
        private AddCommentClickListener(Panel commentPanel, String parentId) {
            this.commentPanel = commentPanel;
            this.parentId = parentId;
        }

        public void onClick(Widget w) {
            showAddComment(commentPanel, parentId, this);
        }

        public boolean isShowingComment() {
            return showingComment;
        }

        public void setShowingComment(boolean showingComment) {
            this.showingComment = showingComment;
        }
        
    }
}
