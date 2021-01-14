const path = require("path");

module.exports = {
	devServer: {
		proxy: {
			"/api": {
				target: "http://localhost:8080"
			},
			"/fhir": {
				target: "http://localhost:8080"
			}
		}
	},
	lintOnSave: process.env.NODE_ENV !== "production",
	css: {
		loaderOptions: {
			less: {
				lessOptions: {
					modifyVars: {
						hack: `true; @import '${path.resolve("./src/assets/less/vant-theme/index.less")}';`
					}
				}
			}
		}
	}
};
