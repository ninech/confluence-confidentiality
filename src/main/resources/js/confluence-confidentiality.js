/* global AJS:false, Confluence:false */

const pictogram = confidentiality => {
  switch (confidentiality) {
    case "public":
      return "eye";
    case "internal":
      return "eye_hidden";
    case "confidential":
      return "lock";
    default:
      return "clipboard";
  }
};

const capitalize = str =>
  str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();

AJS.toInit($ => {
  const dialogId = "confluence-confidentiality-dialog";
  const webItem = $("#confluence-confidentiality");
  const url = `${AJS.contextPath()}/rest/confluence-confidentiality/1.0/confluence-confidentiality?pageId=${AJS.Meta.get(
    "page-id"
  )}`;

  let dataLoaded = false; // only load inline dialog contents once
  let dialog;
  let currentConfidentiality;
  let userCanEdit = false;
  let eventRegistered = false;

  const updateLabelAndIcon = response => {
    currentConfidentiality = response.confidentiality;
    userCanEdit = response.canUserEdit;

    const iconNode = webItem.children("img");
    const textNode = webItem.children("span");

    const picture = `${pictogram(
      response.confidentiality
    )}_blue_solid_16x16.png`;
    iconNode.attr(
      "src",
      `${AJS.contextPath()}/download/resources/ch.nine.confluence-confidentiality:confluence-confidentiality-resources/images/${picture}`
    );
    textNode.text(capitalize(response.confidentiality));

    registerDialogOpenEvent();
  };

  const loadConfidentiality = () => {
    $.ajax({
      url: url,
      type: "GET",
      dataType: "json",
      contentType: "application/json"
    }).done(updateLabelAndIcon);
  };

  const registerDialogOpenEvent = () => {
    if (eventRegistered) {
      return;
    } else {
      eventRegistered = true;
    }

    if (userCanEdit) {
      const saveFn = (theForm, dialogContent) => {
        return () => {
          // Show spinner
          dialogContent.html(
            Confluence.Templates.Plugins.ConfluenceConfidentiality.loading()
          );
          dialogContent.find(".spinner").spin("medium");
          dataLoaded = false;

          $.ajax({
            url: url,
            type: "POST",
            dataType: "json",
            data: $.param(theForm.find("input[checked], input[type!=radio]"))
          })
            .fail(jqXHR => {
              dataLoaded = false;
              if (jqXHR.status === 403) {
                dialogContent.html(
                  Confluence.Templates.Plugins.ConfluenceConfidentiality.forbidden()
                );
              } else {
                dialogContent.html(
                  Confluence.Templates.Plugins.ConfluenceConfidentiality.changeError()
                );
              }
            })
            .done(response => {
              closeDialog();
              updateLabelAndIcon(response);
            });

          return false;
        };
      };
      dialog = AJS.InlineDialog(
        webItem,
        dialogId,
        (content, trigger, showPopup) => {
          if (!dataLoaded) {
            content.html(
              Confluence.Templates.Plugins.ConfluenceConfidentiality.loading()
            );
            content.find(".spinner").spin("medium");
            $.ajax({
              url: url,
              type: "GET",
              dataType: "json"
            })
              .fail(() =>
                content.html(
                  Confluence.Templates.Plugins.ConfluenceConfidentiality.loadError()
                )
              )
              .done(response => {
                content.html(
                  Confluence.Templates.Plugins.ConfluenceConfidentiality.show({
                    pageId: AJS.Meta.get("page-id"),
                    atlToken: AJS.Meta.get("atl-token"),
                    possibleConfidentialityNames: response.possibleConfidentialities.reduce(
                      (names, c) => {
                        names[c] = capitalize(c);
                        return names;
                      },
                      []
                    ),
                    ...response
                  })
                );

                const theForm = content.find(
                  "#confluence-confidentiality-form"
                );

                content
                  .find("#confluence-confidentiality-submit")
                  .click(saveFn(theForm, content));
                dataLoaded = true;
              });
          } else {
            content
              .find(
                `#confluence-confidentiality-radio-${currentConfidentiality}`
              )
              .prop("checked", true);
          }
          showPopup();
          return false;
        }
      );
      webItem.click(closeDialog);
    } else {
      webItem
        .click(() => false)
        .attr("aria-disabled", "true")
        .prop("disabled", true);
    }
  };
  const closeDialog = () => {
    if ($(`#inline-dialog-${dialogId}`).is(":visible")) {
      dialog.hide();
    }
  };

  loadConfidentiality();
});
