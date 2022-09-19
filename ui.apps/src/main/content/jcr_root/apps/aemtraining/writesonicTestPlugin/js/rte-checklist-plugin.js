(function (RTE, Class) {
    const GROUP = 'checkList';
    const CHECKED_FEATURE = 'checkeditem';
    const UNCHECKED_FEATURE = 'uncheckeditem';

    RTE.plugins.checkListPlugin = new Class({

        toString: 'checkListPlugin',

        extend: RTE.plugins.Plugin,

        /**
         * @private
         */
        checkedUI: null,

        /**
         * @private
         */
        uncheckedUI: null,

        uncheckedUI2: null,

        _init: function (editorKernel) {
            this.inherited(arguments);
        },

        getFeatures: function () {
            return [CHECKED_FEATURE, UNCHECKED_FEATURE, "Whatever"];
        },

        initializeUI: function (tbGenerator) {
            if (this.isFeatureEnabled(CHECKED_FEATURE)) {
                this.checkedUI = tbGenerator.createElement(CHECKED_FEATURE, this, true, this.getTooltip(CHECKED_FEATURE));
                tbGenerator.addElement(GROUP, 165, this.checkedUI, 10);
            }
            if (this.isFeatureEnabled(UNCHECKED_FEATURE)) {
                this.uncheckedUI = tbGenerator.createElement(UNCHECKED_FEATURE, this, true, this.getTooltip(UNCHECKED_FEATURE));
                tbGenerator.addElement(GROUP, 165, this.uncheckedUI, 20);
            }
                this.uncheckedUI2 = tbGenerator.createElement("Whatever", this, true, this.getTooltip("Whatever"));
                tbGenerator.addElement(GROUP, 165, this.uncheckedUI2, 20);


            tbGenerator.registerIcon(GROUP + '#' + CHECKED_FEATURE, 'check');
            tbGenerator.registerIcon(GROUP + '#' + UNCHECKED_FEATURE, 'close');
            tbGenerator.registerIcon(GROUP + '#' + "Whatever", 'close');
        },

        notifyPluginConfig: function (pluginConfig) {
            pluginConfig = pluginConfig || {};
            RTE.Utils.applyDefaults(pluginConfig, {
                'features': '*',
                'keepStructureOnUnlist': false,
                'tooltips': {
                    'checkeditem': {
                        'title': 'Checked item',
                        'text': 'Checked item'
                    },
                    'uncheckeditem': {
                        'title': 'Unchecked item',
                        'text': 'Unchecked item'
                    }
                }
            });
            this.config = pluginConfig;
        },

        execute: function (cmd) {
            this.editorKernel.relayCmd(cmd);
        },

        updateState: function (selDef) {
            this.updateUI(this.checkedUI, selDef);
            this.updateUI(this.uncheckedUI, selDef);
        },

        updateUI: function (commandUI, selDef) {
            if (!commandUI) return;

            commandUI.setSelected(false);

            const isTarget = this.isValidTarget(selDef.nodeList);

            commandUI.$ui.toggleClass(RTE.Theme.TOOLBARITEM_DISABLED_CLASS, !isTarget);
            commandUI.$ui.attr('disabled', isTarget ? null : 'disabled');
        },

        isValidTarget: function (nodeList) {
            if (!nodeList) return false;

            const targetGroup = nodeList.commonAncestor;
            if (!targetGroup) return false;

            return !!(targetGroup.closest('li') && !targetGroup.closest('ol'));
        }
    });

// register plugin
    RTE.plugins.PluginRegistry.register(GROUP, RTE.plugins.checkListPlugin);
})(CUI.rte, window.Class);
